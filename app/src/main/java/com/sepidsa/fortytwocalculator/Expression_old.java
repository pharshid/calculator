package com.sepidsa.fortytwocalculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.StringTokenizer;

public class Expression_old {
    /*
    * Strings used for storing expression.
    */
    private boolean PERCENT_FLAG = false;
    private boolean mRad_flag  ;
    public String s, x;
    public BigDecimal last;
    /*
    * Term evaluator for number literals.
    */

    BigDecimal term(){
        BigDecimal ans = new BigDecimal(0);
        StringBuffer temp = new StringBuffer();

        if( s.length() > 0 && s.charAt( 0 ) == 'e') {
            s = s.substring(1);
            ans =new BigDecimal( Math.E);
        } else if( s.length() > 0 && s.charAt( 0 ) == 'π'){
            s = s.substring( 1 );
            ans =new BigDecimal( Math.PI);

        }else{
            while( s.length() > 0 && Character.isDigit( s.charAt( 0 ) ) ){
                temp.append(Integer.parseInt( "" + s.charAt( 0 ) ));
                s = s.substring( 1 );
            }
            if( s.length() > 0 && s.charAt( 0 ) == '.' ){
                temp.append( '.' );
                s = s.substring( 1 );
                while( s.length() > 0 && Character.isDigit( s.charAt( 0 ) ) ){
                    temp.append(Integer.parseInt( "" + s.charAt( 0 ) ));
                    s = s.substring( 1 );
                }
            }
            if( s.length() > 0 && (s.charAt(0) == 'e' || s.charAt(0) == 'E') ){
                temp.append( 'e' );
                s = s.substring( 1 );
                temp.append( s.charAt( 0 ) );
                s = s.substring( 1 );
                while( s.length() > 0 && Character.isDigit( s.charAt( 0 ) ) ){
                    temp.append(Integer.parseInt( "" + s.charAt( 0 ) ));
                    s = s.substring( 1 );

                }
            }
           /* if(s.length() > 0 && (s.charAt(0) == ')')){
                ans = (double)1;
                return ans;

            }
*/
            ans = new BigDecimal(temp.toString());
            //Double.valueOf( temp.toString() ).doubleValue();

        }
        return ans;
    }

	/*
	* Parentheses solver.
	*/

    /* double percentage(){
         double ans = term();
         while( s.length() > 0 ){
             if( s.charAt( 0 ) == '!' ) {
                 s = s.substring(1);
                 ans = compute_factorial(ans);


             }else{
                 break;
             }
         }
         return ans;
     }*/
    BigDecimal factorial(){
        BigDecimal ans = term();
        while( s.length() > 0 ){
            if( s.charAt( 0 ) == '!' ) {
//                s = s.substring(1);
//                ans = compute_factorial(ans);
            }else if ( s.charAt( 0 ) == '%'){
                PERCENT_FLAG = true;
                //s = s.substring(1);
                break;
            }else {
                break;
            }
        }
        return ans;
    }

    /*public BigDecimal compute_factorial(BigDecimal n) {
        double[] fact = {1, 1, 2, 6, 24, 120, 720, 5040, 40320,
                362880, 3628800, 39916800, 479001600};
        return fact[n.intValue()];
    }
*/
    BigDecimal paren(){
        BigDecimal ans = null;
        if(s.length()==0)   {
            int i =3;

        }else {

            if (s.charAt(0) == '(') {
                s = s.substring(1);
                ans = add();
                if (s.length()>0 && s.charAt(0) == ')') {
                    s = s.substring(1);
                }
            }  else {
                ans = factorial();
            }
        }
        return ans;
    }

    /*
	* Exponentiation solver.
	*/
    BigDecimal exp(){
        boolean neg = false;
        if( s.charAt( 0 ) == '−' || s.charAt(0) == '-' ){
            neg = true;
            s = s.substring( 1 );
        }
        BigDecimal ans = paren();
        while( s.length() > 0 ){
            if( s.indexOf( "^" ) == 0 ){
                s = s.substring( 1 );
                boolean expNeg = false;
                // Checking with both types of minus charecter (they look the same here but in real life they look diff
                if( s.charAt( 0 ) == '−' || s.charAt(0) == '-' ){
                    expNeg = true;
                    s = s.substring( 1 );
                }
                BigDecimal e = paren();

                if( ans.doubleValue() < 0 ){		// if it's negative
                    BigDecimal x = new BigDecimal(1);
                    if( Math.ceil(e.doubleValue()) == e.doubleValue() ){	// only raise to an integer
                        if( expNeg )
                            e = e.multiply(new BigDecimal(-1));
                        if( e.doubleValue() == 0 )
                            ans= new BigDecimal(1);
                        else if( e.doubleValue() > 0 )
                            for( int i = 0; i < e.doubleValue(); i++ )
                                x = x.multiply( ans );
                        else
                            for( int i = 0; i < -e.doubleValue(); i++ )
                                x = x.divide(ans,new MathContext(6));
                        ans = x;
                    } else {
                        ans = new BigDecimal(Math.log(-1));	// otherwise make it NaN
                    }
                }
                else if( expNeg ) {
                    ans = new BigDecimal(Math.exp(e.multiply(new BigDecimal(Math.log(ans.doubleValue()))).negate().doubleValue()));
                }
                else{
//                    ans = e.multiply(new BigDecimal( Math.log(ans.doubleValue()))) ;
                    ans = e.pow(-2) ;
//                    ans =   BigFunctions.exp( BigFunctions.ln(e,100).multiply(ans),100 );
                }
            } else
                break;
        }
        if( neg )
            ans = ans.negate();
        return ans;
    }

    /*
    * Trigonometric function solver.
    */
    BigDecimal trig(){
        BigDecimal ans = new BigDecimal(0);
        boolean found = false;
        if( s.indexOf( "sin" ) == 0 ){
            s = s.substring( 3 );
            if(mRad_flag){
                ans = new BigDecimal(Math.sin(trig().doubleValue()));
            }else {
                ans = new BigDecimal( Math.sin(Math.toRadians(trig().doubleValue())));
            }
            found = true;
        } else if( s.indexOf( "cos" ) == 0 ){
            s = s.substring( 3 );
            if(mRad_flag){
                ans = new BigDecimal(Math.cos(trig().doubleValue()));
            }else {
                ans = new BigDecimal( Math.cos(Math.toRadians(trig().doubleValue())));
            }
            found = true;
        } else if( s.indexOf( "tan" ) == 0 ) {
            s = s.substring(3);
            if (mRad_flag) {
                ans = new BigDecimal(Math.tan(trig().doubleValue()));
            } else {
                ans = new BigDecimal( Math.tan(Math.toRadians(trig().doubleValue())));
            }
            found = true;

        } else if( s.indexOf( "cot" ) == 0 ){
            s = s.substring( 3 );
            if(mRad_flag){
                ans = new BigDecimal( 1.0 / Math.tan(trig().doubleValue()));
            }else {
                ans = new BigDecimal(1.0 / Math.tan(Math.toRadians(trig().doubleValue())));
            }
            found = true;

        } else if( s.indexOf( "log" ) == 0 ){
            s = s.substring( 3 );
            ans = new BigDecimal(Math.log10(trig().doubleValue()));
            found = true;
        }else if( s.indexOf("ln") == 0 ){
            s = s.substring( 2 );
            ans = new BigDecimal(Math.log(trig().doubleValue()));
            found = true;
        }else if (s.charAt(0) == '\u221a') {
            s = s.substring(1);
            ans = new BigDecimal(Math.sqrt(trig().doubleValue()));
            found = true;
        }
        if( !found ){
            ans = exp();
        }
        return ans;
    }


    /*
    * Multiplication, division expression solver.
    */
    BigDecimal mul(){
        BigDecimal tempAnswer = new BigDecimal(0);
        BigDecimal ans = trig();


        if( s.length() > 0 ){

//            For 20% * 3
            if((s.length()>1 && s.charAt(1) == '\u00d7') )
                if( (s.charAt(0) == '%'))
                {
                    PERCENT_FLAG = false;
                    ans = ans.divide(new BigDecimal(100),new MathContext(6));
                    s = s.substring( 1 );
                }

            while( s.length() > 0 ){

                if( s.charAt( 0 ) == '\u00d7' || s.charAt( 0 ) == '*' ){
                    s = s.substring( 1 );

                    tempAnswer = trig();

                    if(PERCENT_FLAG == true){
                        ans = (ans.divide(new BigDecimal(100),new MathContext(6)).multiply(tempAnswer));
                        PERCENT_FLAG = false;
                        tempAnswer = new BigDecimal(0);
                        s = s.substring( 1 );
                    }else {
                        // if(s.length()>0)
                        ans = ans.multiply( tempAnswer);

                    }


                } else if( s.charAt( 0 ) == '/'|| s.charAt( 0 ) == '÷' ) {
                    s = s.substring(1);
                    tempAnswer = trig();

                    if (PERCENT_FLAG == true) {
                        ans = (ans.multiply(new BigDecimal(100),new MathContext(6)).divide(tempAnswer));
                        PERCENT_FLAG = false;
                        tempAnswer = new BigDecimal(0);
                        s = s.substring( 1 );
                    }
                    else {
                        //  tempAnswer = trig();
                        // if(s.length()>0)
                        ans = ans.divide( tempAnswer,new MathContext(6));
                    }
                }


                //2 % 300
                // 2 * 3%
                // 20% + 2
                // 2 + 10%
                // 2%



                else{
                    //  s= s.substring( 1 );
                    if (s.charAt(0) != '+'
                            && s.charAt(0) != '−'
                            && s.charAt(0) != ')'
                            && s.charAt(0) != '%') {

                        ans = ans.multiply(mul());
                    }


                    break;
                }
            }
        }
        return ans;
    }

    /*
    * Addition, subtraction expression solver.
    */
    BigDecimal add(){
        BigDecimal tempAnswer = new BigDecimal(0);
        BigDecimal ans = mul();

        // for 20% + 200
        if((s.length()== 1  ) ||
                (s.length()>1 &&( (s.charAt(1) == '+')||(s.charAt(1) == '\u00d7')
                        ||(s.charAt(1) == '−') || (s.charAt(1) == '÷'))  ) )
            if( (s.charAt(0) == '%'))
            {
                PERCENT_FLAG = false;
                ans = ans.divide(new BigDecimal(100),new MathContext(6));
                s = s.substring( 1 );
            }


        while( s.length() > 0 ){

            if( s.charAt( 0 ) == '+' )
            {
                s = s.substring( 1 );
                tempAnswer = mul();

                // for 200 + 2%
                if(PERCENT_FLAG == true){
                    ans = ans.add((ans.multiply(tempAnswer)).divide(new BigDecimal(100),new MathContext(6)));
                    PERCENT_FLAG = false;
                    s = s.substring( 1 );
                }else {
                    // if(s.length()>0)
                    ans = ans.add( tempAnswer);
                }

            } else if( s.charAt( 0 ) == '−' || s.charAt(0) == '-' ){
                s = s.substring( 1 );

                // for 200 - 2%
                tempAnswer = mul();
                if(PERCENT_FLAG == true){
                    ans = ans.subtract((ans.multiply( tempAnswer)).divide(new BigDecimal(100),new MathContext(6)));
                    PERCENT_FLAG = false;
                    s = s.substring( 1 );
                    tempAnswer = new BigDecimal(0);
                }else {
                    // if(s.length()>0)
                    ans = ans.subtract( tempAnswer);
                }
            }

            // for 2 % 200
            else if(s.charAt( 0 ) == '%' ){
                s = s.substring( 1 );
                tempAnswer = mul();
                // if(s.length()>0)
                ans = (ans.divide(new BigDecimal(100),new MathContext(6)).multiply(tempAnswer));
                PERCENT_FLAG = false;
            }

            else{
                // 2%*5
                /*if( s.charAt( 0 ) != ')'){
                    // s = s.substring( 1 );
                    ans = ans.multiply( mul());
                    PERCENT_FLAG = false;


                }else {
                    break;
                }
*/
                break;
            }
        }
        return ans;
    }




    /*
    * Public access method to evaluate this expression.
    */
    public BigDecimal evaluate(){
        s = x.intern();
        s= s.replace(",","");
        last = add();
        return last;
    }

    /*
    * Creates new Expression.
    */
    public Expression_old(String s, boolean _isRad_flag){
        // remove white space, assume only spaces or tabs
        mRad_flag = _isRad_flag;
        StringBuffer b = new StringBuffer();
        StringTokenizer t = new StringTokenizer( s, " " );
        while( t.hasMoreElements() )
            b.append( t.nextToken() );
        t = new StringTokenizer( b.toString(), "\t" );
        b = new StringBuffer();
        while( t.hasMoreElements() )
            b.append( t.nextToken() );

        x = b.toString();
    }

    /*
    * The String value of this Expression.
    */
    public String toString(){
        return x.intern();
    }

    /*
    * Test our Expression class by evaluating the command-line
    * argument and then returning.
    */

}