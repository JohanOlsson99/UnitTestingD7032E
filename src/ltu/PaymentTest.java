package ltu;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import java.util.Calendar;

public class PaymentTest {
    private CalendarImpl cal = new CalendarImpl();

    @Test
    public void testAgeRestrictionsAround20() throws IOException {
        PaymentImpl payment = new PaymentImpl(this.cal);
        assertEquals(0, payment.getMonthlyAmount("20020101-0000", 0, 100, 100));    // age < 20
        assertEquals(9904, payment.getMonthlyAmount("20010101-0000", 0, 100, 100));    // age == 20
        assertEquals(9904, payment.getMonthlyAmount("20000101-0000", 0, 100, 100));    // age > 20
        
    }

    @Test
    public void testAgeRestrictionsAround47() throws IOException {
        PaymentImpl payment = new PaymentImpl(this.cal);
        assertEquals(9904, payment.getMonthlyAmount("19750101-0000", 0, 100, 100));    // age < 47
        assertEquals(2816, payment.getMonthlyAmount("19740101-0000", 0, 100, 100));    // age == 47
        assertEquals(2816, payment.getMonthlyAmount("19730101-0000", 0, 100, 100));    // age > 47
    }

    @Test
    public void testAgeRestrictionsAround56() throws IOException {
        PaymentImpl payment = new PaymentImpl(this.cal);
        assertEquals(2816, payment.getMonthlyAmount("19660101-0000", 0, 100, 100));    // age < 56
        assertEquals(2816, payment.getMonthlyAmount("19650101-0000", 0, 100, 100));    // age == 56
        assertEquals(0, payment.getMonthlyAmount("19640101-0000", 0, 100, 100));    // age > 56
    }
    
    @Test
    public void testIncomeRestrictionsFullTime() throws IOException {
        PaymentImpl payment = new PaymentImpl(this.cal);
        assertEquals(9904, payment.getMonthlyAmount("20000101-0000", 85812, 100, 100));    // income <
        assertEquals(9904, payment.getMonthlyAmount("20000101-0000", 85813, 100, 100));    // income ==
        assertEquals(0, payment.getMonthlyAmount("20000101-0000", 85814, 100, 100));    // income >
    }
    
    @Test
    public void testIncomeRestrictionsPartTime() throws IOException {
        PaymentImpl payment = new PaymentImpl(this.cal);
        assertEquals(4960, payment.getMonthlyAmount("20000101-0000", 128721, 50, 100));    // income <
        assertEquals(4960, payment.getMonthlyAmount("20000101-0000", 128722, 50, 100));    // income ==
        assertEquals(0, payment.getMonthlyAmount("20000101-0000", 128723, 50, 100));    // income >
    }
    
    @Test
    public void testStudyRateRestrictionsPartTime() throws IOException {
        PaymentImpl payment = new PaymentImpl(this.cal);
        assertEquals(0, payment.getMonthlyAmount("20000101-0000", 0, 49, 100));    // study rate <
        assertEquals(4960, payment.getMonthlyAmount("20000101-0000", 0, 50, 100));    // study rate ==
        assertEquals(4960, payment.getMonthlyAmount("20000101-0000", 0, 51, 100));    // study rate >
    }
    
    @Test
    public void testStudyRateRestrictionsFullTime() throws IOException {
        PaymentImpl payment = new PaymentImpl(this.cal);
        assertEquals(4960, payment.getMonthlyAmount("20000101-0000", 0, 99, 100));    // study rate <
        assertEquals(9904, payment.getMonthlyAmount("20000101-0000", 0, 100, 100));    // study rate ==
        assertEquals(9904, payment.getMonthlyAmount("20000101-0000", 0, 101, 100));    // study rate >
    }
    
    @Test
    public void testCompletionRatioRestrictions() throws IOException {
        PaymentImpl payment = new PaymentImpl(this.cal);
        assertEquals(0, payment.getMonthlyAmount("20000101-0000", 0, 100, 49));    // completion ratio <
        assertEquals(9904, payment.getMonthlyAmount("20000101-0000", 0, 100, 50));    // completion ratio ==
        assertEquals(9904, payment.getMonthlyAmount("20000101-0000", 0, 100, 51));    // completion ratio >
    }
    
    @Test
    public void testError() throws IOException {
        int counter = 0;
        PaymentImpl payment = new PaymentImpl(new CalendarTest());
        try {
            payment.getMonthlyAmount(null, 0, 100, 100);    // error thrown
            // fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input.", e.getMessage());
            counter++;
        }

        try {
            payment.getMonthlyAmount("20000101-0000", -1, 100, 100);    // error thrown
            // fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input.", e.getMessage());
            counter++;
        }

        try {
            payment.getMonthlyAmount("20000101-0000", 0, -1, 100);    // error thrown
            // fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input.", e.getMessage());
            counter++;
        }

        try {
            payment.getMonthlyAmount("20000101-0000", 0, 100, -1);    // error thrown
            // fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input.", e.getMessage());
            counter++;
        }
        
        try{
            payment.getMonthlyAmount("200001010000", 0, 100, 0);    // error thrown
            // fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid personId: 200001010000", e.getMessage());
            counter++;
        }
        
        try{
            payment.getMonthlyAmount("20000101-00000", 0, 100, 0);    // error thrown
            // fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid personId: 20000101-00000", e.getMessage());
            counter++;
        }
        assertEquals(6, counter);

    }

    @Test
    public void testErrorConstructor() throws IOException {
        int counter = 0;
        try {
            new PaymentImpl(this.cal, null);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                counter++;
            }
        }
        assertEquals(1, counter);
    }

    @Test
    public void testPaymentDay() throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.set(2016, Calendar.JANUARY, 1); // Sunday
        CalendarTest calTest = new CalendarTest(cal);
        assertEquals(new PaymentImpl(calTest).getNextPaymentDay(), "20160129");
        cal.set(2016, Calendar.APRIL, 1); // Saturday
        assertEquals(new PaymentImpl(calTest).getNextPaymentDay(), "20160429");
    }
}
