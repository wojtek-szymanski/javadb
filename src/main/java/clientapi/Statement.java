package clientapi;

import executors.*;
import sqlparser.SQLParseException;
import sqlparser.SQLParserImpl;
import sqlparser.SelectCommand;
import sqlparser.AbstractSQLCommand;

/**
 * Created on 2014-10-23.
 */
public class Statement {

    private ExecutionContext context;

    public int getNextFetchSize() {
        return context.nextFetchSize;
    }

    public void setNextFetchSize(int nextFetchSize) {
        this.context.nextFetchSize = nextFetchSize;
    }

    QueryCommandExecutor queryExecutor = CommandExecutorFactory.getQueryExecutorInstance();

    /**
     * Executes the given SQL SELECT statement, which returns a single ResultSet object.
     * @param sql
     * @return
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        try {
            try {
                SelectCommand selectCommand = (SelectCommand) parse(sql);
                return queryExecutor.executeQuery(selectCommand, context);
            } catch (ClassCastException e) { //TODO: handle it differently
                throw new SQLException("Cannot call executeQuery method with non SELECT statement");
            }
        } catch (SQLParseException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement or an SQL statement that returns nothing, such as an SQL DDL statement.
     * @param sql
     * @return number of rows changed.
     */
    public void executeUpdate(String sql) throws SQLException {
        try {
            AbstractSQLCommand SQLCommand = parse(sql);
            CommandExecutor executor = CommandExecutorFactory.getInstance(SQLCommand.getType());
            executor.execute(SQLCommand);
        } catch (SQLParseException e) {
            throw new SQLException(e);
        }
    }

    public void execute(String sql) throws SQLException {
         executeUpdate(sql);
    }

    private AbstractSQLCommand parse(String sql) {
        SQLParserImpl parser = new SQLParserImpl();
        return parser.parse(sql);
    }
}
