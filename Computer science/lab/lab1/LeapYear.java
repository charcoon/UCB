public class LeapYear {
    public static void main(String[] args) {
        int year = 2000;
        boolean leapyear = false;
        if(year % 4 == 0)
        {if( year % 100 == 0)
            {if ( year % 400 == 0)leapyear = true;
                else leapyear = false;}
            else leapyear = true;}
        else
            leapyear = false;


        if(leapyear)
            System.out.println(year + " is a leap year");
        else
            System.out.println(year + " is not a leap year");
    }
}