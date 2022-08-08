/** P2Pattern class
 *  @author Josh Hug & Vivant Sakore
 */


/** P2Pattern class
 * 1. For P1:

Define a pattern that matches valid dates of the form MM/DD/YYYY
For example, 12/25/2019 is a valid date but 25/12/2019 is not.
Assume that MM ranges from 01-12, DD ranges from 01-31 and YYYY ranges from 1900 onwards.
2. For P2:

Define a pattern that matches lists of non-negative numerals in the notation we used in homework 2 (e.g. (1, 2, 33, 1, 63)).
Each numeral but the last should be followed by a comma and one or more spaces.
3. For P3:

Define a pattern that matches a valid domain name.
For example, www.support.ucb-login.com is a valid domain name (even if it doesn't really exist!)
A valid domain name contains set of alphanumeric characters (i.e., a-z, A-Z), numbers (i.e. 0-9) and dashes (-) or a combination of all of these.
It does not begin or end with dash (-) or period (.)
It does not contain whitespace ( ) or underscore (_)
Assume that the top-level domain (last part after period) is between 2 to 6 characters in length.
4. For P4:

Define a pattern that matches a valid Java variable name
For example, _myVariable$1 is a valid variable name in Java while 1stVariable is not.
A variable name cannot start with an integer. It can consist of alphanumeric characters as well as _ and $.
5. For P5:

Define a pattern that matches valid IPv4 address.
For example, 127.0.0.1 is a valid IP address whereas 299.10.10.1 is not.
A valid IPv4 address consists of four positive integer parts separated by period (.). Each integer part can range from 0-255.
 */




public class P2Pattern {
    /* Pattern to match a valid date of the form MM/DD/YYYY. Eg: 9/22/2019 */
    public static final String P1 = "^(?:[1-9]|0[1-9]|1[0-2])/(?:[0-9]|0[1-9]|1[0-9]|2[0-9]|3[0-1])/(?:20[6-9][0-9]|20[0-1][0-4])$";

    /** Pattern to match 61b notation for literal IntLists. */
    public static final String P2 = "\\(([0-9]+, +)+[0-9]+\\)";

    /* Pattern to match a valid domain name. Eg: www.support.facebook-login.com */
    public static final String P3 = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";

    /* Pattern to match a valid java variable name. Eg: _child13$ */
    public static final String P4 = "^[a-zA-Z_$][a-zA-Z_$0-9]*$";

    /* Pattern to match a valid IPv4 address. Eg: 127.0.0.1 */
    public static final String P5 = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";

}
