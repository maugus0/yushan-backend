# Novel Creation User Journey Flow Analysis

## System Overview

Based on codebase analysis, the Yushan Backend system supports novel creation process with the following main components:

### 1. User Roles and Permissions
- **USER**: Regular user
- **AUTHOR**: Author who can create and manage novels
- **ADMIN**: Administrator with full privileges

### 2. Main Entities
- **User**: User information with `isAuthor`, `isAdmin` fields
- **Novel**: Novel with DRAFT/UNDER_REVIEW/PUBLISHED/HIDDEN status (ARCHIVED removed)
- **Chapter**: Novel chapters
- **Category**: Novel classification categories

## User Journey Flow

```mermaid
graph TD
    A[User Registration/Login] --> B{User Role Check}
    B -->|Regular User| C[Upgrade to Author]
    B -->|Author/Admin| D[Novel Creation Process]
    
    C --> C1[Send Email Verification]
    C1 --> C2[Enter Verification Code]
    C2 --> C3[Upgrade to Author Role]
    C3 --> D
    
    D --> D1[Create Novel Basic Info]
    D1 --> D2[Set Novel Details]
    D2 --> D3[Novel Created as DRAFT]
    D3 --> D4[Add Chapters]
    D4 --> D5{User Role Check}
    
    D5 -->|Admin| D6[Admin: Direct Publish]
    D5 -->|Author| D7[Author: Submit for Review]
    
    D6 --> D6a[Status: DRAFT → PUBLISHED]
    D6 --> D6b[Set Publish Time]
    D6 --> D6c[Update Novel Stats]
    
    D7 --> D7a[Status: DRAFT → UNDER_REVIEW]
    D7a --> D8[Admin Review Process]
    D8 --> D9{Admin Decision}
    D9 -->|Approve| D10[Status: UNDER_REVIEW → PUBLISHED]
    D9 -->|Reject| D11[Status: UNDER_REVIEW → DRAFT]
    D9 -->|Hide| D12[Status: → HIDDEN]
    
    D1 --> D1a[Title - Required]
    D1 --> D1b[Category - Required]
    D1 --> D1c[Synopsis - Optional]
    D1 --> D1d[Cover Image - Optional]
    D1 --> D1e[Completion Status - Optional]
    
    D4 --> D4a[Chapter Title]
    D4 --> D4b[Chapter Content]
    D4 --> D4c[Premium Settings]
    D4 --> D4d[Pricing Settings]
```

## PlantUML Sequence Diagrams

### 1. Complete Novel Creation Process

```plantuml
@startuml Novel Creation Process
!theme plain
title Novel Creation User Journey

actor User as U
participant Frontend as F
participant NovelController as NC
participant NovelService as NS
participant NovelMapper as NM
participant CategoryMapper as CM
database Database as DB

== 1. Novel Creation Process ==
U -> F: Create Novel Form
F -> NC: POST /api/novels (with JWT token)

note over NC: Security Check
NC -> NC: @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")

note over NC: Extract User Info
NC -> NC: Extract userId and authorName from JWT

note over NC: Validation
NC -> NC: Validate NovelCreateRequestDTO
note right of NC
- title (required)
- categoryId (required)
- synopsis (max 4000 chars)
- coverImgUrl (valid URL)
- isCompleted (optional)
end note

NC -> NS: createNovel(userId, authorName, request)

note over NS: Business Logic
NS -> CM: selectByPrimaryKey(categoryId)
CM -> DB: Check category exists
DB --> CM: Category details
CM --> NS: Category validation

alt Category not found
    NS --> NC: IllegalArgumentException("category not found")
    NC --> F: 400 Bad Request
    F --> U: Category not found error
else Category exists
    note over NS: Create Novel Entity
    NS -> NS: Create new Novel object
    note right of NS
    Set default values:
    - status: DRAFT
    - chapterCnt: 0
    - wordCnt: 0
    - avgRating: 0.0
    - viewCnt: 0
    - voteCnt: 0
    - yuanCnt: 0.0
    - isValid: true
    - publishTime: null
    end note
    
    NS -> NM: insertSelective(novel)
    NM -> DB: INSERT INTO novel
    DB --> NM: Novel created with ID
    NM --> NS: Novel saved
    
    NS -> NS: Convert to NovelDetailResponseDTO
    NS --> NC: NovelDetailResponseDTO
    NC --> F: 201 Created + Novel details
    F --> U: Novel created successfully
end

== 2. Novel Publishing Process ==
note over U,DB: Current Flow - Only Admin can change status directly
U -> F: Publish Novel
F -> NC: PUT /api/novels/{id} (with status: PUBLISHED)
NC -> NC: Check if user is ADMIN
alt User is Admin
    NC -> NS: updateNovel(id, request)
    NS -> NM: updateByPrimaryKeySelective(novel)
    NM -> DB: UPDATE novel SET status = 2, publish_time = NOW()
    DB --> NM: Novel updated
    NM --> NS: Update successful
    NS --> NC: NovelDetailResponseDTO
    NC --> F: 200 OK + Updated novel
    F --> U: Novel published successfully
else User is not Admin
    NC --> F: 403 Forbidden - Only Admin can change status
    F --> U: Access denied - Admin privileges required
end

== 3. Admin Approval Workflow (Future Enhancement) ==
note over U,DB: Enhanced Flow with Admin Approval

== 3.1. Author Submits for Review ==
U -> F: Submit for Review
F -> NC: POST /api/novels/{id}/submit-review
NC -> NS: submitForReview(novelId, userId)
NS -> NM: updateByPrimaryKeySelective(novel)
NM -> DB: UPDATE novel SET status = 1 (UNDER_REVIEW)
DB --> NM: Novel updated
NM --> NS: Update successful
NS --> NC: NovelDetailResponseDTO
NC --> F: 200 OK + Novel under review
F --> U: Novel submitted for review

== 3.2. Admin Reviews Novel ==
actor Admin as A
A -> F: View Novels Under Review
F -> NC: GET /api/novels/admin/under-review
NC -> NS: getNovelsUnderReview()
NS -> NM: selectByStatus(1) // UNDER_REVIEW
NM -> DB: SELECT * FROM novel WHERE status = 1
DB --> NM: Novels under review
NM --> NS: List of novels
NS --> NC: List<NovelDetailResponseDTO>
NC --> F: 200 OK + Novels list
F --> A: Display novels for review

== 3.3. Admin Approves Novel ==
A -> F: Approve Novel
F -> NC: POST /api/novels/{id}/approve
NC -> NS: approveNovel(novelId)
NS -> NM: updateByPrimaryKeySelective(novel)
NM -> DB: UPDATE novel SET status = 2, publish_time = NOW()
DB --> NM: Novel approved and published
NM --> NS: Update successful
NS --> NC: NovelDetailResponseDTO
NC --> F: 200 OK + Novel approved
F --> A: Novel approved and published

== 3.4. Admin Rejects Novel ==
A -> F: Reject Novel
F -> NC: POST /api/novels/{id}/reject
NC -> NS: rejectNovel(novelId)
NS -> NM: updateByPrimaryKeySelective(novel)
NM -> DB: UPDATE novel SET status = 0 (DRAFT)
DB --> NM: Novel rejected, back to draft
NM --> NS: Update successful
NS --> NC: NovelDetailResponseDTO
NC --> F: 200 OK + Novel rejected
F --> A: Novel rejected, author can edit and resubmit

== 4. Future Implementation ==
note over U,DB: Chapter Creation (Not yet implemented)
note over U,DB: Advanced Publishing Features
note over U,DB: Email Notifications for Approval Status

@enduml
```

### 2. Detailed API Flow Sequence

```plantuml
@startuml API Flow Sequence
!theme plain
title Detailed Novel Creation & Publishing API Flow

participant Client as C
participant NovelController as NC
participant NovelService as NS
participant NovelMapper as NM
participant CategoryMapper as CM
database Database as DB

== 1. Novel Creation ==
C -> NC: POST /api/novels
note over NC: Security & Validation
NC -> NC: Check JWT token
NC -> NC: Extract user details
NC -> NC: Validate request DTO

NC -> NS: createNovel(userId, authorName, request)

note over NS: Business Logic
NS -> CM: selectByPrimaryKey(categoryId)
CM -> DB: SELECT * FROM category WHERE id = ?
DB --> CM: Category record
CM --> NS: Category object

alt Category exists
    note over NS: Create Novel Entity
    NS -> NS: new Novel()
    NS -> NS: Set all properties
    NS -> NS: Set default values (status: DRAFT)
    
    NS -> NM: insertSelective(novel)
    NM -> DB: INSERT INTO novel VALUES (...)
    DB --> NM: Generated ID
    NM --> NS: Insert successful
    
    NS -> NS: Convert to DTO
    NS --> NC: NovelDetailResponseDTO
    NC --> C: 201 Created + Novel data
else Category not found
    NS --> NC: IllegalArgumentException
    NC --> C: 400 Bad Request
end

== 2. Novel Publishing (Admin Only) ==
C -> NC: PUT /api/novels/{id} (status: PUBLISHED)
note over NC: Check Admin Role
NC -> NC: Check if user is ADMIN

alt User is Admin
    NC -> NS: updateNovel(id, request)
    NS -> NM: updateByPrimaryKeySelective(novel)
    NM -> DB: UPDATE novel SET status = 2, publish_time = NOW()
    DB --> NM: Novel updated
    NM --> NS: Update successful
    NS --> NC: NovelDetailResponseDTO
    NC --> C: 200 OK + Updated novel
else User is not Admin
    NC --> C: 403 Forbidden - Only Admin can change status
end

== 3. Author Submit for Review (Future) ==
C -> NC: POST /api/novels/{id}/submit-review
NC -> NS: submitForReview(novelId, userId)
NS -> NM: updateByPrimaryKeySelective(novel)
NM -> DB: UPDATE novel SET status = 1 (UNDER_REVIEW)
DB --> NM: Novel updated
NM --> NS: Update successful
NS --> NC: NovelDetailResponseDTO
NC --> C: 200 OK + Novel under review

== 4. Admin Approve Novel (Future) ==
C -> NC: POST /api/novels/{id}/approve
NC -> NS: approveNovel(novelId)
NS -> NM: updateByPrimaryKeySelective(novel)
NM -> DB: UPDATE novel SET status = 2, publish_time = NOW()
DB --> NM: Novel approved and published
NM --> NS: Update successful
NS --> NC: NovelDetailResponseDTO
NC --> C: 200 OK + Novel approved

@enduml
```

### 3. User Journey Flow Diagram

```plantuml
@startuml User Journey Flow
!theme plain
title Novel Creation User Journey Flow

start

:User Registration/Login;
if (User Role Check) then (Regular User)
    :Send Email Verification;
    :Enter Verification Code;
    :Upgrade to Author Role;
else (Author/Admin)
    :Novel Creation Process;
endif

:Create Novel Basic Info;
note right
- Title (Required)
- Category (Required)
- Synopsis (Optional)
- Cover Image (Optional)
- Completion Status (Optional)
end note

:Set Novel Details;
:Novel Created as DRAFT;

:Add Chapters;
note right
- Chapter Title
- Chapter Content
- Premium Settings
- Pricing Settings
end note

if (User Role Check) then (Admin)
    :Admin: Direct Publish;
    note right
    - Status: DRAFT → PUBLISHED
    - Set Publish Time
    - Update Novel Stats
    end note
else (Author)
    :Author: Submit for Review;
    note right
    - Status: DRAFT → UNDER_REVIEW
    - Wait for Admin Decision
    end note
    
    :Admin Review Process;
    if (Admin Decision) then (Approve)
        :Status: UNDER_REVIEW → PUBLISHED;
        :Set Publish Time;
        :Update Novel Stats;
    elseif (Reject) then (yes)
        :Status: UNDER_REVIEW → DRAFT;
        :Author can Edit and Resubmit;
    else (Hide)
        :Status: → HIDDEN;
    endif
endif

stop

@enduml
```

### 4. System Architecture Diagram

```plantuml
@startuml System Architecture
!theme plain
title Yushan Backend System Architecture

package "Frontend Layer" {
    [Web Application] as Web
    [Mobile App] as Mobile
}

package "API Gateway" {
    [Spring Security] as Security
    [JWT Authentication] as JWT
}

package "Controller Layer" {
    [AuthController] as AuthC
    [NovelController] as NovelC
    [AuthorController] as AuthorC
    [LibraryController] as LibraryC
}

package "Service Layer" {
    [AuthService] as AuthS
    [NovelService] as NovelS
    [AuthorService] as AuthorS
    [LibraryService] as LibraryS
}

package "Data Access Layer" {
    [UserMapper] as UserM
    [NovelMapper] as NovelM
    [ChapterMapper] as ChapterM
    [CategoryMapper] as CategoryM
}

package "Database" {
    database "PostgreSQL" as DB {
        [Users Table] as Users
        [Novels Table] as Novels
        [Chapters Table] as Chapters
        [Categories Table] as Categories
    }
}

Web --> Security
Mobile --> Security
Security --> JWT
JWT --> AuthC
JWT --> NovelC
JWT --> AuthorC
JWT --> LibraryC

AuthC --> AuthS
NovelC --> NovelS
AuthorC --> AuthorS
LibraryC --> LibraryS

AuthS --> UserM
NovelS --> NovelM
NovelS --> CategoryM
AuthorS --> UserM
LibraryS --> NovelM

UserM --> Users
NovelM --> Novels
ChapterM --> Chapters
CategoryM --> Categories

@enduml
```

### 5. Database Entity Relationship

```plantuml
@startuml Database ERD
!theme plain
title Database Entity Relationship Diagram

entity "users" {
    * uuid : UUID <<PK>>
    --
    * email : VARCHAR(255)
    * username : VARCHAR(100)
    * hash_password : VARCHAR(255)
    email_verified : BOOLEAN
    avatar_url : VARCHAR(500)
    profile_detail : TEXT
    birthday : DATE
    gender : INTEGER
    status : INTEGER
    is_author : BOOLEAN
    is_admin : BOOLEAN
    level : INTEGER
    exp : DOUBLE
    yuan : DOUBLE
    read_time : DOUBLE
    read_book_num : INTEGER
    create_time : TIMESTAMP
    update_time : TIMESTAMP
    last_login : TIMESTAMP
    last_active : TIMESTAMP
}

entity "category" {
    * id : SERIAL <<PK>>
    --
    * name : VARCHAR(100)
    description : VARCHAR(255)
    slug : VARCHAR(100)
    is_active : BOOLEAN
    create_time : TIMESTAMP
    update_time : TIMESTAMP
}

entity "novel" {
    * id : SERIAL <<PK>>
    --
    * uuid : UUID
    * title : VARCHAR(255)
    * author_id : UUID <<FK>>
    author_name : VARCHAR(100)
    * category_id : INTEGER <<FK>>
    synopsis : TEXT
    cover_img_url : VARCHAR(500)
    * status : INTEGER
    is_completed : BOOLEAN
    is_valid : BOOLEAN
    chapter_cnt : INTEGER
    word_cnt : BIGINT
    avg_rating : REAL
    review_cnt : INTEGER
    view_cnt : BIGINT
    vote_cnt : INTEGER
    yuan_cnt : REAL
    create_time : TIMESTAMP
    update_time : TIMESTAMP
    publish_time : TIMESTAMP
}

entity "chapter" {
    * id : SERIAL <<PK>>
    --
    * uuid : UUID
    * novel_id : INTEGER <<FK>>
    * chapter_number : INTEGER
    title : VARCHAR(255)
    content : TEXT
    word_cnt : INTEGER
    is_premium : BOOLEAN
    yuan_cost : REAL
    view_cnt : BIGINT
    is_valid : BOOLEAN
    create_time : TIMESTAMP
    update_time : TIMESTAMP
    publish_time : TIMESTAMP
}

entity "library" {
    * id : SERIAL <<PK>>
    --
    * uuid : UUID
    * user_id : UUID <<FK>>
    create_time : TIMESTAMP
    update_time : TIMESTAMP
}

entity "novel_library" {
    * id : SERIAL <<PK>>
    --
    * library_id : INTEGER <<FK>>
    * novel_id : INTEGER <<FK>>
    * progress : INTEGER
    create_time : TIMESTAMP
    update_time : TIMESTAMP
}

users ||--o{ novel : "author_id"
category ||--o{ novel : "category_id"
novel ||--o{ chapter : "novel_id"
users ||--o{ library : "user_id"
library ||--o{ novel_library : "library_id"
novel ||--o{ novel_library : "novel_id"

@enduml
```

## Novel Status Flow

### Status Definitions
- **DRAFT**: Novel is being created/edited by author
- **UNDER_REVIEW**: Novel submitted by author, waiting for admin approval
- **PUBLISHED**: Novel approved and visible to public
- **HIDDEN**: Novel hidden by admin (not visible to public)

### Enhanced Workflow with Admin Approval

#### For Authors:
1. Create novel → Status: DRAFT
2. Add chapters and content
3. Submit for review → Status: DRAFT → UNDER_REVIEW
4. Wait for admin decision

#### For Admins:
1. Create novel → Status: DRAFT (can publish directly)
2. Review novels under review
3. Approve → Status: UNDER_REVIEW → PUBLISHED
4. Reject → Status: UNDER_REVIEW → DRAFT
5. Hide → Status: → HIDDEN

### Status Transitions
```
DRAFT → UNDER_REVIEW (Author submits)
UNDER_REVIEW → PUBLISHED (Admin approves)
UNDER_REVIEW → DRAFT (Admin rejects)
UNDER_REVIEW → HIDDEN (Admin hides)
PUBLISHED → HIDDEN (Admin hides)
HIDDEN → PUBLISHED (Admin unhides)
```

## API Endpoints Mapping

### Authentication & Authorization
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/author/upgrade-to-author` - Upgrade to author

### Novel Management
- `POST /api/novels` - Create novel (AUTHOR/ADMIN only)
- `GET /api/novels` - List novels (Public)
- `GET /api/novels/{id}` - Get novel details (Public)
- `PUT /api/novels/{id}` - Update novel (Owner/Author/Admin - Only Admin can change status)

### Novel Approval Workflow
- `POST /api/novels/{id}/submit-review` - Submit novel for review (AUTHOR only)
- `POST /api/novels/{id}/approve` - Approve novel for publishing (ADMIN only)
- `POST /api/novels/{id}/reject` - Reject novel (ADMIN only)
- `POST /api/novels/{id}/hide` - Hide novel (ADMIN only)
- `GET /api/novels/admin/under-review` - Get novels under review (ADMIN only)

### Category Management (for novel creation)
- `GET /api/categories/active` - Get active categories (Public)
- `GET /api/categories/{id}` - Get category by ID (Public)