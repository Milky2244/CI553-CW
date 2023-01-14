package clients.clientInclusiveWindow;

import catalogue.Basket;
import middle.MiddleFactory;
import middle.OrderProcessing;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class InclusiveView implements Observer
{
    private static final String PICKED = "Picked";
    private final JLabel      PickAction  = new JLabel();
    private final JTextArea   PickOutput  = new JTextArea();
    private final JScrollPane theSP      = new JScrollPane();
    private static JButton     theBtPicked= new JButton( PICKED );

    private OrderProcessing theOrder     = null;
    private InclusiveController cont= null;

    public static JButton returnBtPicked() {
        return theBtPicked;
    }
    private static final String COLLECT = "Collect";
    private final JLabel      CollectionAction  = new JLabel();
    private final JTextField  CollectionInput   = new JTextField();
    private final JTextArea   CollectionOutput  = new JTextArea();
    private final JScrollPane theSP2      = new JScrollPane();
    private static JButton     theBtCollect= new JButton( COLLECT );
    private static final int H = 600;       // Height of window pixels
    private static final int W = 400;       // Width  of window pixels

    private final JTextField  theInput   = new JTextField();

    public static JButton returnBtCollect() {
        return theBtCollect;
    }
    public static JButton returnBtPicked() {
        return theBtPicked;
    }

    /**
     * Construct the view
     * @param rpc   Window in which to construct
     * @param mf    Factor to deliver order and stock objects
     * @param x     x-cordinate of position of window on screen
     * @param y     y-cordinate of position of window on screen
     */
    public InclusiveView(  RootPaneContainer rpc, MiddleFactory mf, int x, int y )
    {
        try                                           //
        {
            theOrder = mf.makeOrderProcessing();        // Process order
        } catch ( Exception e )
        {
            System.out.println("Exception: " + e.getMessage() );
        }
        Container cp         = rpc.getContentPane();    // Content Pane
        Container rootWindow = (Container) rpc;         // Root Window
        cp.setLayout(null);                             // No layout manager
        rootWindow.setSize( W, H );                     // Size of Window
        rootWindow.setLocation( x, y );

        Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is




        // Start of collect display

        theBtCollect.setBounds( 16, 25+60*0, 80, 40 );  // Check Button
        theBtCollect.addActionListener(                 // Call back code
                e -> cont.doCollect( CollectionInput.getText()) );
        cp.add( theBtCollect );                         //  Add to canvas

        CollectionAction.setBounds( 110, 25 , 270, 20 );       // Message area
        CollectionAction.setText( "" );                        // Blank
        cp.add( CollectionAction );                            //  Add to canvas

        CollectionInput.setBounds( 110, 50, 270, 40 );         // Input Area
        CollectionInput.setText("");                           // Blank
        cp.add( CollectionInput );                             //  Add to canvas

        theSP2.setBounds( 110, 100, 270, 180 );          // Scrolling pane
        CollectionOutput.setText( "" );                        //  Blank
        CollectionOutput.setFont( f );                         //  Uses font
        cp.add( theSP2 );                                //  Add to canvas
        theSP2.getViewport().add( CollectionOutput );           //  In TextArea
        rootWindow.setVisible( true );                  // Make visible
        CollectionInput.requestFocus();                        // Focus is here





        // Start of picked display

        theBtPicked.setBounds( 16, 300+60*0, 80, 40 );   // Check Button
        theBtPicked.addActionListener(                   // Call back code
                e -> cont.doPick() );
        cp.add( theBtPicked );                          //  Add to canvas

        PickAction.setBounds( 110, 250 , 270, 20 );       // Message area
        PickAction.setText( "" );                        // Blank
        cp.add( PickAction );                            //  Add to canvas

        theSP.setBounds( 110, 325, 270, 205 );           // Scrolling pane
        PickOutput.setText( "" );                        //  Blank
        PickOutput.setFont( f );                         //  Uses font
        cp.add( theSP );                                //  Add to canvas
        theSP.getViewport().add( PickOutput );           //  In TextArea
        rootWindow.setVisible( true );                  // Make visible

        try                                           //
        {
            theOrder = mf.makeOrderProcessing();        // Process order
        } catch ( Exception e )
        {
            System.out.println("Exception: " + e.getMessage() );
        }


    }


    public void setController(InclusiveController c )
    {
        cont = c;
    }

    /**
     * Update the view
     * @param modelC   The observed model
     * @param arg      Specific args
     */
    @Override
    public void update( Observable modelC, Object arg )
    {
        InclusiveModel model  = (InclusiveModel) modelC;
        String        message = (String) arg;
        PickAction.setText( message );
        CollectionAction.setText( message );

        CollectionOutput.setText( model.getResponce2() );
        PickOutput.setText( model.getResponce() );
        theInput.requestFocus();               // Focus is here

        Basket basket =  model.getBasket();
        if ( basket != null )
        {
            PickOutput.setText( basket.getDetails() );
        } else {
            PickOutput.setText("");
        }
    }
}
