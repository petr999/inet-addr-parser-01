import java.util.*;
import java.io.*;

/**
 * InetAddrParser - parses addresses supplied on standard input
 */

class InetAddrParser {

  protected static BufferedReader reader;

  // Application's entry point
  public static void main( String[] args ){
    reader = new BufferedReader(new InputStreamReader(System.in));
    TreeSet inetAddrs = new TreeSet();

    while( true ){
      String line = readLineIn( );
      if( Objects.isNull( line ) ){

        break; // read loop

      } else {
        int i = InetAddrInt.convertToInt( line );
        inetAddrs.add( i );
        // System.out.println( line );
      }
    }

    System.out.println( "Unique addresses count: " +  inetAddrs.size() );
  }

  // Read line from standard input
  protected static String readLineIn( ){
    String line = null;

    try {
      line = reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    } 

    return line;
  }
}

/**
 * InetAddrInt - parses ip address into unsigned integer value
 */
class InetAddrInt {

  // Convert String ip address into integer representaion
  public static int convertToInt( String ipAddrStr ){
    int ipAddrInt = 0;

    String[] ipAddrStrArr = ipAddrStr.split( "\\." );
    if( 4 != ipAddrStrArr.length ){
      throw new InputMismatchException( "Wrong input: " + ipAddrStr );
    }
    for( int i = 0; i < ipAddrStrArr.length; i++ ){
        String byteStr = ipAddrStrArr[ i ];
        int byteInt = Integer.parseUnsignedInt( byteStr );
        int shiftTo = 8 * ( 3 - i );
        byteInt = byteInt << shiftTo;
        ipAddrInt += byteInt;
    }

    return ipAddrInt;
  }

}
