package com.keencho.lib.custom.p6spy;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Stack;
import java.util.function.Predicate;

import static java.util.Arrays.stream;

public class CustomP6spyPrettySqlFormatter implements MessageFormattingStrategy {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String P6SPY_FORMATTER = "CustomP6spyPrettySqlFormatter";
    private static final String CREATE = "create";
    private static final String ALTER = "alter";
    private static final String COMMENT = "comment";

    @Override
    public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql, final String url) {
        return sqlFormatToUpper(sql, category, getMessage(connectionId, elapsed, getStackBuilder()));
    }

    private String sqlFormatToUpper(final String sql, final String category, final String message) {
        if (sql.trim().isEmpty()) {
            return "";
        }

        return NEW_LINE +
                sqlFormatToUpper(sql, category) +
                message;
    }

    private String sqlFormatToUpper(final String sql, final String category) {
        if (isStatementDDL(sql, category)) {
            return FormatStyle.DDL
                    .getFormatter()
                    .format(sql)
                    .replace("+0900", "");
        }
        return FormatStyle.BASIC
                .getFormatter()
                .format(sql)
                .replace("+0900", "");
    }

    private boolean isStatementDDL(final String sql, final String category) {
        return isStatement(category) && isDDL(sql.trim().toLowerCase(Locale.ROOT));
    }

    private boolean isStatement(final String category) {
        return Category.STATEMENT.getName().equals(category);
    }

    private boolean isDDL(final String lowerSql) {
        return lowerSql.startsWith(CREATE) || lowerSql.startsWith(ALTER) || lowerSql.startsWith(COMMENT);
    }

    private String getMessage(final int connectionId, final long elapsed, final StringBuilder callStackBuilder) {
        return NEW_LINE +
                NEW_LINE +
                "\t" + String.format("Connection ID: %s", connectionId) +
                NEW_LINE +
                "\t" + String.format("Execution Time: %s ms", elapsed) +
                NEW_LINE +
                NEW_LINE +
                "\t" + String.format("Call Stack (number 1 is entry point): %s", callStackBuilder) +
                NEW_LINE +
                NEW_LINE +
                "----------------------------------------------------------------------------------------------------";
    }

    private StringBuilder getStackBuilder() {
        final Stack<String> callStack = new Stack<>();
        stream(new Throwable().getStackTrace())
                .map(StackTraceElement::toString)
                .filter(isExcludeWords())
                .limit(CustomP6spyProperties.limitStackTrace)
                .forEach(callStack::push);

        int order = 1;
        final StringBuilder callStackBuilder = new StringBuilder();
        while (!callStack.empty()) {
            callStackBuilder.append(MessageFormat.format("{0}\t\t{1}. {2}", NEW_LINE, order++, callStack.pop()));
        }
        return callStackBuilder;
    }

    private Predicate<String> isExcludeWords() {
        return charSequence ->
                charSequence.startsWith(CustomP6spyProperties.startPackage) &&
                checkExcludePackages(charSequence) &&
                !charSequence.contains(P6SPY_FORMATTER) &&
                !charSequence.contains("<generated>") &&
                !charSequence.contains("Unknown Source");
    }

    private boolean checkExcludePackages(String charSequence) {
        return stream(CustomP6spyProperties.excludePackages).noneMatch(charSequence::contains);
    }
}
