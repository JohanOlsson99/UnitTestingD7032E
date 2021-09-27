package ltu;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Calendar;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

// import org.mockito.Mockito;
// import org.powermock.api.mockito.PowerMockito;

/*@RunWith(PowerMockRunner.class)
@PrepareForTest({CalendarImpl.class})*/
public class PaymentTest {
    private CalendarImpl cal = new CalendarImpl();

    
    @Test
    public void testGetLoad() throws IOException {        
        // ICalendar cal1 = new ICalendar();
        PaymentImpl payment = new PaymentImpl(cal);
        // int test = payment.getMonthlyAmount("19990101-0000", 0, 0, 0);
        assertEquals(0, payment.getMonthlyAmount("19990101-0000", 0, 0, 0));
    }

    @Test
    public void testAgeRestrictionsAround20() throws IOException {
        PaymentImpl payment = new PaymentImpl(this.cal);
        assertEquals(9904, payment.getMonthlyAmount("20000101-0000", 0, 100, 100));    // age > 20
        assertEquals(9904, payment.getMonthlyAmount("20010101-0000", 0, 100, 100));    // age == 20
        assertEquals(0, payment.getMonthlyAmount("20020101-0000", 0, 100, 100));    // age < 20
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
        assertEquals(9904, payment.getMonthlyAmount("20000101-0000", 85813, 100, 100));    // income <
        assertEquals(9904, payment.getMonthlyAmount("20000101-0000", 85812, 100, 100));    // income ==
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
        PaymentImpl payment = new PaymentImpl(this.cal);
        try {
            payment.getMonthlyAmount(null, 0, 100, 100);    // error thrown
            fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input.", e.getMessage());
        }

        try {
            payment.getMonthlyAmount("20000101-0000", -1, 100, 100);    // error thrown
            fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input.", e.getMessage());
        }

        try {
            payment.getMonthlyAmount("20000101-0000", 0, -1, 100);    // error thrown
            fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input.", e.getMessage());
        }

        try {
            payment.getMonthlyAmount("20000101-0000", 0, 100, -1);    // error thrown
            fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input.", e.getMessage());
        }
        
        try{
            payment.getMonthlyAmount("200001010000", 0, 100, 0);    // error thrown
            fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid personId: 200001010000", e.getMessage());
        }
        
        try{
            payment.getMonthlyAmount("20000101-00000", 0, 100, 0);    // error thrown
            fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid personId: 20000101-00000", e.getMessage());
        }
    }

    @Test
    public void testErrorConstructor() throws IOException {
        try {
            new PaymentImpl(this.cal, null);
            fail("Expected exception did not occur");   // if no error thrown, cast a fail
        } catch (Exception e) { }
    }

    @Test @After
    public void testPaymentDay() throws IOException {
        System.out.println("cal " + this.cal.getDate());
        /*mockStatic(Calendar.class);
        when(Calendar.getInstance()).thenReturn(calendar);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // day
        calendar.set(Calendar.MONTH, 1); // month
        calendar.set(Calendar.YEAR, 1999); // year        
        PaymentImpl payment = new PaymentImpl(this.cal);
        System.out.println(payment.getNextPaymentDay());*/
        
        
        Calendar endOfMarch = Calendar.getInstance();
        endOfMarch.set(2011, Calendar.MARCH, 27);
        PowerMockito.mockStatic(Calendar.class);
        Mockito.when(Calendar.getInstance()).thenReturn(endOfMarch);
        System.out.println("date: " + endOfMarch);
        Calendar newDate = Calendar.getInstance();
        System.out.println("date: " + newDate);
    }

}
