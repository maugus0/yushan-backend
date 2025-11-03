package com.yushan.backend.config;

import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UUIDTypeHandlerTest {

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private CallableStatement callableStatement;

    private UUIDTypeHandler uuidTypeHandler;
    private final UUID testUUID = UUID.randomUUID();
    private final String uuidString = testUUID.toString();
    private final String columnName = "uuid_column";
    private final int columnIndex = 1;

    @BeforeEach
    void setUp() {
        uuidTypeHandler = new UUIDTypeHandler();
    }

    @Test
    void setNonNullParameter_shouldSetObject() throws SQLException {
        // When
        uuidTypeHandler.setNonNullParameter(preparedStatement, columnIndex, testUUID, JdbcType.OTHER);

        // Then
        verify(preparedStatement).setObject(columnIndex, testUUID);
    }

    @Test
    void getNullableResult_fromResultSetByColumnName_shouldReturnUUID() throws SQLException {
        // Given
        when(resultSet.getObject(columnName)).thenReturn(testUUID);

        // When
        UUID result = uuidTypeHandler.getNullableResult(resultSet, columnName);

        // Then
        assertEquals(testUUID, result);
    }

    @Test
    void getNullableResult_fromResultSetByColumnName_whenString_shouldReturnUUID() throws SQLException {
        // Given
        when(resultSet.getObject(columnName)).thenReturn(uuidString);

        // When
        UUID result = uuidTypeHandler.getNullableResult(resultSet, columnName);

        // Then
        assertEquals(testUUID, result);
    }

    @Test
    void getNullableResult_fromResultSetByColumnName_whenNull_shouldReturnNull() throws SQLException {
        // Given
        when(resultSet.getObject(columnName)).thenReturn(null);

        // When
        UUID result = uuidTypeHandler.getNullableResult(resultSet, columnName);

        // Then
        assertNull(result);
    }

    @Test
    void getNullableResult_fromResultSetByColumnIndex_shouldReturnUUID() throws SQLException {
        // Given
        when(resultSet.getObject(columnIndex)).thenReturn(testUUID);

        // When
        UUID result = uuidTypeHandler.getNullableResult(resultSet, columnIndex);

        // Then
        assertEquals(testUUID, result);
    }

    @Test
    void getNullableResult_fromResultSetByColumnIndex_whenString_shouldReturnUUID() throws SQLException {
        // Given
        when(resultSet.getObject(columnIndex)).thenReturn(uuidString);

        // When
        UUID result = uuidTypeHandler.getNullableResult(resultSet, columnIndex);

        // Then
        assertEquals(testUUID, result);
    }

    @Test
    void getNullableResult_fromResultSetByColumnIndex_whenNull_shouldReturnNull() throws SQLException {
        // Given
        when(resultSet.getObject(columnIndex)).thenReturn(null);

        // When
        UUID result = uuidTypeHandler.getNullableResult(resultSet, columnIndex);

        // Then
        assertNull(result);
    }

    @Test
    void getNullableResult_fromCallableStatement_shouldReturnUUID() throws SQLException {
        // Given
        when(callableStatement.getObject(columnIndex)).thenReturn(testUUID);

        // When
        UUID result = uuidTypeHandler.getNullableResult(callableStatement, columnIndex);

        // Then
        assertEquals(testUUID, result);
    }

    @Test
    void getNullableResult_fromCallableStatement_whenString_shouldReturnUUID() throws SQLException {
        // Given
        when(callableStatement.getObject(columnIndex)).thenReturn(uuidString);

        // When
        UUID result = uuidTypeHandler.getNullableResult(callableStatement, columnIndex);

        // Then
        assertEquals(testUUID, result);
    }

    @Test
    void getNullableResult_fromCallableStatement_whenNull_shouldReturnNull() throws SQLException {
        // Given
        when(callableStatement.getObject(columnIndex)).thenReturn(null);

        // When
        UUID result = uuidTypeHandler.getNullableResult(callableStatement, columnIndex);

        // Then
        assertNull(result);
    }
}
