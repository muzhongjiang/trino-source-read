/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.spi.connector;

import io.trino.spi.security.Privilege;
import io.trino.spi.security.TrinoPrincipal;
import io.trino.spi.security.ViewExpression;
import io.trino.spi.type.Type;

import java.util.Optional;
import java.util.Set;

import static io.trino.spi.security.AccessDeniedException.denyAddColumn;
import static io.trino.spi.security.AccessDeniedException.denyCommentColumn;
import static io.trino.spi.security.AccessDeniedException.denyCommentTable;
import static io.trino.spi.security.AccessDeniedException.denyCreateMaterializedView;
import static io.trino.spi.security.AccessDeniedException.denyCreateRole;
import static io.trino.spi.security.AccessDeniedException.denyCreateSchema;
import static io.trino.spi.security.AccessDeniedException.denyCreateTable;
import static io.trino.spi.security.AccessDeniedException.denyCreateView;
import static io.trino.spi.security.AccessDeniedException.denyCreateViewWithSelect;
import static io.trino.spi.security.AccessDeniedException.denyDeleteTable;
import static io.trino.spi.security.AccessDeniedException.denyDropColumn;
import static io.trino.spi.security.AccessDeniedException.denyDropMaterializedView;
import static io.trino.spi.security.AccessDeniedException.denyDropRole;
import static io.trino.spi.security.AccessDeniedException.denyDropSchema;
import static io.trino.spi.security.AccessDeniedException.denyDropTable;
import static io.trino.spi.security.AccessDeniedException.denyDropView;
import static io.trino.spi.security.AccessDeniedException.denyExecuteProcedure;
import static io.trino.spi.security.AccessDeniedException.denyGrantRoles;
import static io.trino.spi.security.AccessDeniedException.denyGrantSchemaPrivilege;
import static io.trino.spi.security.AccessDeniedException.denyGrantTablePrivilege;
import static io.trino.spi.security.AccessDeniedException.denyInsertTable;
import static io.trino.spi.security.AccessDeniedException.denyRefreshMaterializedView;
import static io.trino.spi.security.AccessDeniedException.denyRenameColumn;
import static io.trino.spi.security.AccessDeniedException.denyRenameSchema;
import static io.trino.spi.security.AccessDeniedException.denyRenameTable;
import static io.trino.spi.security.AccessDeniedException.denyRenameView;
import static io.trino.spi.security.AccessDeniedException.denyRevokeRoles;
import static io.trino.spi.security.AccessDeniedException.denyRevokeSchemaPrivilege;
import static io.trino.spi.security.AccessDeniedException.denyRevokeTablePrivilege;
import static io.trino.spi.security.AccessDeniedException.denySelectColumns;
import static io.trino.spi.security.AccessDeniedException.denySetCatalogSessionProperty;
import static io.trino.spi.security.AccessDeniedException.denySetRole;
import static io.trino.spi.security.AccessDeniedException.denySetSchemaAuthorization;
import static io.trino.spi.security.AccessDeniedException.denySetTableAuthorization;
import static io.trino.spi.security.AccessDeniedException.denySetViewAuthorization;
import static io.trino.spi.security.AccessDeniedException.denyShowColumns;
import static io.trino.spi.security.AccessDeniedException.denyShowCreateSchema;
import static io.trino.spi.security.AccessDeniedException.denyShowCreateTable;
import static io.trino.spi.security.AccessDeniedException.denyShowCurrentRoles;
import static io.trino.spi.security.AccessDeniedException.denyShowRoleAuthorizationDescriptors;
import static io.trino.spi.security.AccessDeniedException.denyShowRoleGrants;
import static io.trino.spi.security.AccessDeniedException.denyShowRoles;
import static io.trino.spi.security.AccessDeniedException.denyShowSchemas;
import static io.trino.spi.security.AccessDeniedException.denyShowTables;
import static io.trino.spi.security.AccessDeniedException.denyUpdateTableColumns;
import static java.util.Collections.emptySet;

public interface ConnectorAccessControl
{
    /**
     * Check if identity is allowed to create the specified schema in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanCreateSchema(ConnectorSecurityContext context, String schemaName)
    {
        denyCreateSchema(schemaName);
    }

    /**
     * Check if identity is allowed to drop the specified schema in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanDropSchema(ConnectorSecurityContext context, String schemaName)
    {
        denyDropSchema(schemaName);
    }

    /**
     * Check if identity is allowed to rename the specified schema in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanRenameSchema(ConnectorSecurityContext context, String schemaName, String newSchemaName)
    {
        denyRenameSchema(schemaName, newSchemaName);
    }

    /**
     * Check if identity is allowed to change the specified schema's user/role.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanSetSchemaAuthorization(ConnectorSecurityContext context, String schemaName, TrinoPrincipal principal)
    {
        denySetSchemaAuthorization(schemaName, principal);
    }

    /**
     * Check if identity is allowed to execute SHOW SCHEMAS in a catalog.
     * <p>
     * NOTE: This method is only present to give users an error message when listing is not allowed.
     * The {@link #filterSchemas} method must handle filter all results for unauthorized users,
     * since there are multiple way to list schemas.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanShowSchemas(ConnectorSecurityContext context)
    {
        denyShowSchemas();
    }

    /**
     * Filter the list of schemas to those visible to the identity.
     */
    default Set<String> filterSchemas(ConnectorSecurityContext context, Set<String> schemaNames)
    {
        return emptySet();
    }

    /**
     * Check if identity is allowed to execute SHOW CREATE SCHEMA.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanShowCreateSchema(ConnectorSecurityContext context, String schemaName)
    {
        denyShowCreateSchema(schemaName);
    }

    /**
     * Check if identity is allowed to execute SHOW CREATE TABLE, SHOW CREATE VIEW or SHOW CREATE MATERIALIZED VIEW
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanShowCreateTable(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyShowCreateTable(tableName.toString(), null);
    }

    /**
     * Check if identity is allowed to create the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanCreateTable(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyCreateTable(tableName.toString());
    }

    /**
     * Check if identity is allowed to drop the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanDropTable(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyDropTable(tableName.toString());
    }

    /**
     * Check if identity is allowed to rename the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanRenameTable(ConnectorSecurityContext context, SchemaTableName tableName, SchemaTableName newTableName)
    {
        denyRenameTable(tableName.toString(), newTableName.toString());
    }

    /**
     * Check if identity is allowed to comment the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanSetTableComment(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyCommentTable(tableName.toString());
    }

    /**
     * Check if identity is allowed to comment the column in the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanSetColumnComment(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyCommentColumn(tableName.toString());
    }

    /**
     * Check if identity is allowed to show metadata of tables by executing SHOW TABLES, SHOW GRANTS etc. in a catalog.
     * <p>
     * NOTE: This method is only present to give users an error message when listing is not allowed.
     * The {@link #filterTables} method must filter all results for unauthorized users,
     * since there are multiple ways to list tables.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanShowTables(ConnectorSecurityContext context, String schemaName)
    {
        denyShowTables(schemaName);
    }

    /**
     * Filter the list of tables and views to those visible to the identity.
     */
    default Set<SchemaTableName> filterTables(ConnectorSecurityContext context, Set<SchemaTableName> tableNames)
    {
        return emptySet();
    }

    /**
     * Check if identity is allowed to show columns of tables by executing SHOW COLUMNS, DESCRIBE etc.
     * <p>
     * NOTE: This method is only present to give users an error message when listing is not allowed.
     * The {@link #filterColumns} method must filter all results for unauthorized users,
     * since there are multiple ways to list columns.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanShowColumns(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyShowColumns(tableName.getTableName());
    }

    /**
     * Filter the list of columns to those visible to the identity.
     */
    default Set<String> filterColumns(ConnectorSecurityContext context, SchemaTableName tableName, Set<String> columns)
    {
        return emptySet();
    }

    /**
     * Check if identity is allowed to add columns to the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanAddColumn(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyAddColumn(tableName.toString());
    }

    /**
     * Check if identity is allowed to drop columns from the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanDropColumn(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyDropColumn(tableName.toString());
    }

    /**
     * Check if identity is allowed to change the specified table's user/role.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanSetTableAuthorization(ConnectorSecurityContext context, SchemaTableName tableName, TrinoPrincipal principal)
    {
        denySetTableAuthorization(tableName.toString(), principal);
    }

    /**
     * Check if identity is allowed to rename a column in the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanRenameColumn(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyRenameColumn(tableName.toString());
    }

    /**
     * Check if identity is allowed to select from the specified columns in a relation.  The column set can be empty.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanSelectFromColumns(ConnectorSecurityContext context, SchemaTableName tableName, Set<String> columnNames)
    {
        denySelectColumns(tableName.toString(), columnNames);
    }

    /**
     * Check if identity is allowed to insert into the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanInsertIntoTable(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyInsertTable(tableName.toString());
    }

    /**
     * Check if identity is allowed to delete from the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanDeleteFromTable(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        denyDeleteTable(tableName.toString());
    }

    /**
     * Check if identity is allowed to update the supplied columns in the specified table in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanUpdateTableColumns(ConnectorSecurityContext context, SchemaTableName tableName, Set<String> updatedColumns)
    {
        denyUpdateTableColumns(tableName.toString(), updatedColumns);
    }

    /**
     * Check if identity is allowed to create the specified view in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanCreateView(ConnectorSecurityContext context, SchemaTableName viewName)
    {
        denyCreateView(viewName.toString());
    }

    /**
     * Check if identity is allowed to rename the specified view in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanRenameView(ConnectorSecurityContext context, SchemaTableName viewName, SchemaTableName newViewName)
    {
        denyRenameView(viewName.toString(), newViewName.toString());
    }

    /**
     * Check if identity is allowed to change the specified view's user/role.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanSetViewAuthorization(ConnectorSecurityContext context, SchemaTableName viewName, TrinoPrincipal principal)
    {
        denySetViewAuthorization(viewName.toString(), principal);
    }

    /**
     * Check if identity is allowed to drop the specified view in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanDropView(ConnectorSecurityContext context, SchemaTableName viewName)
    {
        denyDropView(viewName.toString());
    }

    /**
     * Check if identity is allowed to create a view that selects from the specified columns in a relation.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanCreateViewWithSelectFromColumns(ConnectorSecurityContext context, SchemaTableName tableName, Set<String> columnNames)
    {
        denyCreateViewWithSelect(tableName.toString(), context.getIdentity());
    }

    /**
     * Check if identity is allowed to create the specified materialized view in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanCreateMaterializedView(ConnectorSecurityContext context, SchemaTableName materializedViewName)
    {
        denyCreateMaterializedView(materializedViewName.toString());
    }

    /**
     * Check if identity is allowed to refresh the specified materialized view in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanRefreshMaterializedView(ConnectorSecurityContext context, SchemaTableName materializedViewName)
    {
        denyRefreshMaterializedView(materializedViewName.toString());
    }

    /**
     * Check if identity is allowed to drop the specified materialized view in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanDropMaterializedView(ConnectorSecurityContext context, SchemaTableName materializedViewName)
    {
        denyDropMaterializedView(materializedViewName.toString());
    }

    /**
     * Check if identity is allowed to set the specified property in this catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanSetCatalogSessionProperty(ConnectorSecurityContext context, String propertyName)
    {
        denySetCatalogSessionProperty(propertyName);
    }

    /**
     * Check if identity is allowed to grant to any other user the specified privilege on the specified schema.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanGrantSchemaPrivilege(ConnectorSecurityContext context, Privilege privilege, String schemaName, TrinoPrincipal grantee, boolean grantOption)
    {
        denyGrantSchemaPrivilege(privilege.toString(), schemaName);
    }

    default void checkCanRevokeSchemaPrivilege(ConnectorSecurityContext context, Privilege privilege, String schemaName, TrinoPrincipal revokee, boolean grantOption)
    {
        denyRevokeSchemaPrivilege(privilege.toString(), schemaName);
    }

    /**
     * Check if identity is allowed to grant to any other user the specified privilege on the specified table.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanGrantTablePrivilege(ConnectorSecurityContext context, Privilege privilege, SchemaTableName tableName, TrinoPrincipal grantee, boolean grantOption)
    {
        denyGrantTablePrivilege(privilege.toString(), tableName.toString());
    }

    /**
     * Check if identity is allowed to revoke the specified privilege on the specified table from any user.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanRevokeTablePrivilege(ConnectorSecurityContext context, Privilege privilege, SchemaTableName tableName, TrinoPrincipal revokee, boolean grantOption)
    {
        denyRevokeTablePrivilege(privilege.toString(), tableName.toString());
    }

    default void checkCanCreateRole(ConnectorSecurityContext context, String role, Optional<TrinoPrincipal> grantor)
    {
        denyCreateRole(role);
    }

    default void checkCanDropRole(ConnectorSecurityContext context, String role)
    {
        denyDropRole(role);
    }

    default void checkCanGrantRoles(ConnectorSecurityContext context, Set<String> roles, Set<TrinoPrincipal> grantees, boolean adminOption, Optional<TrinoPrincipal> grantor, String catalogName)
    {
        denyGrantRoles(roles, grantees);
    }

    default void checkCanRevokeRoles(ConnectorSecurityContext context, Set<String> roles, Set<TrinoPrincipal> grantees, boolean adminOption, Optional<TrinoPrincipal> grantor, String catalogName)
    {
        denyRevokeRoles(roles, grantees);
    }

    default void checkCanSetRole(ConnectorSecurityContext context, String role, String catalogName)
    {
        denySetRole(role);
    }

    /**
     * Check if identity is allowed to show role authorization descriptors (i.e. RoleGrants).
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanShowRoleAuthorizationDescriptors(ConnectorSecurityContext context, String catalogName)
    {
        denyShowRoleAuthorizationDescriptors(catalogName);
    }

    /**
     * Check if identity is allowed to show roles on the specified catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanShowRoles(ConnectorSecurityContext context, String catalogName)
    {
        denyShowRoles(catalogName);
    }

    /**
     * Check if identity is allowed to show current roles on the specified catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanShowCurrentRoles(ConnectorSecurityContext context, String catalogName)
    {
        denyShowCurrentRoles(catalogName);
    }

    /**
     * Check if identity is allowed to show its own role grants on the specified catalog.
     *
     * @throws io.trino.spi.security.AccessDeniedException if not allowed
     */
    default void checkCanShowRoleGrants(ConnectorSecurityContext context, String catalogName)
    {
        denyShowRoleGrants(catalogName);
    }

    default void checkCanExecuteProcedure(ConnectorSecurityContext context, SchemaRoutineName procedure)
    {
        denyExecuteProcedure(procedure.toString());
    }

    /**
     * Get a row filter associated with the given table and identity.
     * <p>
     * The filter must be a scalar SQL expression of boolean type over the columns in the table.
     *
     * @return the filter, or {@link Optional#empty()} if not applicable
     */
    default Optional<ViewExpression> getRowFilter(ConnectorSecurityContext context, SchemaTableName tableName)
    {
        return Optional.empty();
    }

    /**
     * Get a column mask associated with the given table, column and identity.
     * <p>
     * The mask must be a scalar SQL expression of a type coercible to the type of the column being masked. The expression
     * must be written in terms of columns in the table.
     *
     * @return the mask, or {@link Optional#empty()} if not applicable
     */
    default Optional<ViewExpression> getColumnMask(ConnectorSecurityContext context, SchemaTableName tableName, String columnName, Type type)
    {
        return Optional.empty();
    }
}