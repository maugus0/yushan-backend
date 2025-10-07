# Novel Creation User Journey Flow Analysis

## System Overview

Based on codebase analysis, the Yushan Backend system supports novel creation process with the following main components:

### 1. User Roles and Permissions
- **USER**: Regular user
- **AUTHOR**: Author who can create and manage novels
- **ADMIN**: Administrator with full privileges

### 2. Main Entities
- **User**: User information with `isAuthor`, `isAdmin` fields
- **Novel**: Novel with DRAFT/PUBLISHED/ARCHIVED status
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
    D4 --> D5[Publish Novel]
    
    D1 --> D1a[Title - Required]
    D1 --> D1b[Category - Required]
    D1 --> D1c[Synopsis - Optional]
    D1 --> D1d[Cover Image - Optional]
    D1 --> D1e[Completion Status - Optional]
    
    D4 --> D4a[Chapter Title]
    D4 --> D4b[Chapter Content]
    D4 --> D4c[Premium Settings]
    D4 --> D4d[Pricing Settings]
    
    D5 --> D5a[Status: DRAFT → PUBLISHED]
    D5 --> D5b[Set Publish Time]
    D5 --> D5c[Update Novel Stats]
```

## PlantUML Sequence Diagrams

### 1. Complete Novel Creation Process

```plantuml
@startuml Novel Creation Process
!theme plain
title Novel Creation User Journey

actor User as U
participant Frontend as F
participant AuthController as AC
participant AuthService as AS
participant NovelController as NC
participant NovelService as NS
participant NovelMapper as NM
participant CategoryMapper as CM
database Database as DB

== 1. Authentication & Authorization ==
U -> F: Login Request
F -> AC: POST /api/auth/login
AC -> AS: authenticateUser()
AS -> DB: Validate credentials
DB --> AS: User details
AS --> AC: JWT Token + User roles
AC --> F: Authentication response
F --> U: Login successful

== 2. Author Role Verification ==
alt User is not Author
    U -> F: Request Author Upgrade
    F -> AC: POST /api/author/send-email-author-verification
    AC -> AS: sendAuthorVerificationEmail()
    AS --> AC: Email sent
    AC --> F: Verification email sent
    F --> U: Check email for verification code
    
    U -> F: Enter verification code
    F -> AC: POST /api/author/upgrade-to-author
    AC -> AS: upgradeToAuthor()
    AS -> DB: Update user role to AUTHOR
    DB --> AS: User updated
    AS --> AC: User upgraded
    AC --> F: Author upgrade successful
    F --> U: Now you can create novels
end

== 3. Novel Creation Process ==
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

== 4. Future Implementation ==
note over U,DB: Chapter Creation (Not yet implemented)
note over U,DB: Novel Publishing (Status update)
note over U,DB: Novel Updates (PUT /api/novels/{id})

@enduml
```

### 2. Detailed API Flow Sequence

```plantuml
@startuml API Flow Sequence
!theme plain
title Detailed Novel Creation API Flow

participant Client as C
participant NovelController as NC
participant NovelService as NS
participant NovelMapper as NM
participant CategoryMapper as CM
database Database as DB

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
    NS -> NS: Set default values
    
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

:Publish Novel;
note right
- Status: DRAFT → PUBLISHED
- Set Publish Time
- Update Novel Stats
end note

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

## API Endpoints Mapping

### Authentication & Authorization
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/author/upgrade-to-author` - Upgrade to author

### Novel Management
- `POST /api/novels` - Create novel (AUTHOR/ADMIN only)
- `GET /api/novels` - List novels (Public)
- `GET /api/novels/{id}` - Get novel details (Public)
- `PUT /api/novels/{id}` - Update novel (Owner/Author/Admin)

### Library Management
- `POST /library/{novelId}` - Add novel to library
- `DELETE /library/{novelId}` - Remove novel from library
- `GET /library` - Get personal library list