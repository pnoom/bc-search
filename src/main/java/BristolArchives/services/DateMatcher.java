package BristolArchives.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DateMatcher {
    Date startDate;
    Date endDate;

    public void matchAttempt(String displayDate) {

        String D = "(?:rd|st|nd|th)?";
        String C = "\\[?([a-zA-Z]{2,10})\\]?\\s*(\\d{2,4})\\s*"; //Month YYYY
        String A = "(\\d{1,2})" +D+ "\\s*[-]?\\s*([a-zA-Z]{2,10})\\s*[-]?\\s*(\\d{2,4})\\s*"; //DD Month YYYY (-)
        String B = "\\s*(?:n.d.|nd)?\\s*(c.|c|C|C.)?\\s*(\\d{4})(s)?\\s*([\\(]?circa[\\)]?)?\\s*"; //YYYY
        String E = "\\s*(\\d{1,2})(?:.|\\|/)(\\d{1,2})(?:.|\\|/)(\\d{4})\\s*"; //DD/MM/YYYY

        List<String> patternStrings = new ArrayList<>(Arrays.asList("(\\d{1,2})"+D+"\\s*[-]?\\s*([a-zA-Z]{2,10})\\s*[-]?\\s*(\\d{2,4})\\s*[-]?\\s*"+A //DD Month YYYY - DD Month YYYY
                ,"(\\d{1,2})\\s*[-]?\\s*([a-zA-Z]{2,10})\\s*[-]?\\s*"+A //DD Month - DD Month YYYY
                ,A //DD Month YYYY
                ,"(\\d{0,2})-(\\d{1,2})\\s*[-]?\\s*(\\w{2,10})\\s*[-]?\\s*(\\d{2,4})\\s*" //DD-DD Month YYYY
                ,C+"\\s*[-]?\\s*"+C //Month YYYY - Month YYYY
                ,C+"\\s*[-]?\\s*"+B //Month YYYY - YYYY
                ,C //Month YYYY
                ,"\\[?([a-zA-Z]{2,10})\\]?\\s*[-]?\\s*(\\d{2})\\s*" //Month YY
                ,"\\[?"+B+"-"+B+"\\]?" // YYYY - YYYY
                ,"\\[?"+B+"-\\s*(\\d{2})\\s*\\]?" // YYYY - YY
                ,"\\[?"+B+"\\]?" // YYYY
                ,"(?:[a-zA-Z]+[,]?\\s*)"+A //Location, DD Month YYYY
                ,E+"\\s*[-]?\\s*"+E //DD/MM/YYYY - DD/MM/YYYY
                ,E //DD/MM/YYYY
        ));

        List<Pattern> patterns = patternStrings.stream().map(x -> Pattern.compile(x)).collect(Collectors.toList());

        List<Consumer<Matcher>> handlers = new ArrayList<>(Arrays.asList(handler0,
                handler1, handler2, handler3, handler4, handler5, handler6, handler7, handler8, handler9, handler10, handler11,  handler12,  handler13));

        boolean matches = false;
        for (int i=0; i<patterns.size(); i++) {
            Matcher matcher = patterns.get(i).matcher(displayDate);
            matches = matcher.matches();
            if (matches) {
                handlers.get(i).accept(matcher);
                break;
            }
        }
        // Print all the dates that aren't handled by our current regexes
//            if (!matches) {
//                System.out.println(displayDate);
//            }
    }

    private Date formatDDMonthYY(String day, String mon, String year) {
        Integer month = monthToNumber(mon);
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        //System.out.printf("%4s-%2s-%2s\n", year, month, day);
        try {
            return df.parse(String.format("%4s-%2s-%2s", year, month, day));
        } catch (ParseException exception) {
            throw new RuntimeException("Parse error");
        }
    }

    private Integer monthToNumber(String month) {
        List<String> names = new ArrayList<>(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
        for (String name : names) {
            if (name.contains(month)) {
                return Month.valueOf(name.toUpperCase()).getValue();
            }
        }
        return -1;
    }

    // DD Month YYYY - DD Month YYYY
    Consumer<Matcher> handler0  = matcher -> {
        startDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(3));
        endDate = formatDDMonthYY(matcher.group(4), matcher.group(5), matcher.group(6));
    };

    // DD Month - DD Month YYYY
    Consumer<Matcher> handler1  = matcher -> {
        startDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(5));
        endDate = formatDDMonthYY(matcher.group(3), matcher.group(4), matcher.group(5));
    };

    // DD Month YYYY
    Consumer<Matcher> handler2  = matcher -> {
        startDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(3));
        endDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(3));
    };

    // DD-DD Month YYYY
    Consumer<Matcher> handler3  = matcher -> {
        startDate = formatDDMonthYY(matcher.group(1), matcher.group(3), matcher.group(4));
        endDate = formatDDMonthYY(matcher.group(2), matcher.group(3), matcher.group(4));
    };

    // Month YYYY - Month YYYY
    Consumer<Matcher> handler4  = matcher -> {
        startDate = formatDDMonthYY("1", matcher.group(1), matcher.group(2));
        endDate = formatDDMonthYY("28", matcher.group(3), matcher.group(4));
    };

    // Month YYYY - YYYY
    Consumer<Matcher> handler5  = matcher -> {
        startDate = formatDDMonthYY("1", matcher.group(1), matcher.group(2));
        endDate = formatDDMonthYY("28", matcher.group(1), matcher.group(4));
    };

    // Month YYYY
    Consumer<Matcher> handler6  = matcher -> {
        startDate = formatDDMonthYY("1", matcher.group(1), matcher.group(2));
        endDate = formatDDMonthYY("28", matcher.group(1), matcher.group(2));
    };

    // Month YY
    Consumer<Matcher> handler7  = matcher -> {
        Integer tempMonth = monthToNumber(matcher.group(1));
        Integer tempYear = Integer.parseInt(matcher.group(2));
        tempYear += 1900;
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        try {
            startDate = df.parse(String.format("%4d-%2d-%2d", tempYear, tempMonth, 1));
            endDate = df.parse(String.format("%4d-%2d-%2d", tempYear, tempMonth, 28));
        } catch (ParseException exception) {
            throw new RuntimeException("Parse error");
        }
    };

    // YYYY - YYYY
    Consumer<Matcher> handler8  = matcher -> {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        try {
            startDate = df.parse(String.format("%4s-%2d-%2d", matcher.group(2), 1, 1));
            endDate = df.parse(String.format("%4s-%2d-%2d", matcher.group(6), 12, 31));
        } catch (ParseException exception) {
            throw new RuntimeException("Parse error");
        }
    };

    // YYYY-YY
    Consumer<Matcher> handler9  = matcher -> {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        String centString = matcher.group(2).substring(0,2);
        String decString = matcher.group(5).substring(0,2);
        String concatString = centString.concat(decString);
        try {
            startDate = df.parse(String.format("%4s-%2d-%2d", matcher.group(2), 1, 1));
            endDate = df.parse(String.format("%4s-%2d-%2d", concatString, 12, 31));
        } catch (ParseException exception) {
            throw new RuntimeException("Parse error");
        }
    };

    // YYYY with circa/s inference
    Consumer<Matcher> handler10  = matcher -> {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        Integer startTempYear = Integer.parseInt(matcher.group(2));
        Integer endTempYear = Integer.parseInt(matcher.group(2));

        if ((matcher.group(1) != null || matcher.group(4) != null) && matcher.group(3) != null) {
            startTempYear -= 10;
            endTempYear += 9;
        } else if (matcher.group(1) != null || matcher.group(4) != null) {
            startTempYear -= 2;
            endTempYear += 2;
        } else if (matcher.group(3) != null) {
            endTempYear += 9;
        }
        try {
            startDate = df.parse(String.format("%4d-%2d-%2d", startTempYear, 1, 1));
            endDate = df.parse(String.format("%4d-%2d-%2d", endTempYear, 12, 31));
        } catch (ParseException exception) {
            throw new RuntimeException("Parse error");
        }
    };

    // Location DD Month YYYY (Same code as handler 2)
    Consumer<Matcher> handler11  = matcher -> {
        startDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(3));
        endDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(3));
    };

    // DD/MM/YYYY - DD/MM/YYYY
    Consumer<Matcher> handler12  = matcher -> {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        try {
            startDate = df.parse(String.format("%4s-%2s-%2s", matcher.group(3),matcher.group(2),matcher.group(1)));
            endDate = df.parse(String.format("%4s-%2s-%2s", matcher.group(6),matcher.group(5),matcher.group(4)));
        } catch (ParseException exception) {
            throw new RuntimeException("Parse error");
        }
    };

    // DD/MM/YYYY
    Consumer<Matcher> handler13  = matcher -> {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        try {
            startDate = df.parse(String.format("%4s-%2s-%2s", matcher.group(3),matcher.group(2),matcher.group(1)));
            endDate = df.parse(String.format("%4s-%2s-%2s", matcher.group(3),matcher.group(2),matcher.group(1)));
        } catch (ParseException exception) {
            throw new RuntimeException("Parse error");
        }
    };
}

