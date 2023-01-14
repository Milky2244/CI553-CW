package clients.CashierInclusiveWindow;

import catalogue.Basket;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockReadWriter;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;


/**
 * View of the model
 * @author  M A Smith (c) June 2014  
 */
public class ShopView implements Observer {
  private static final int H = 375;       // Height of window pixels
  private static final int W = 500;       // Width  of window pixels

  private static final String CHECK = "Check Warehouse";
  private static final String BUY = "Buy";
  private static final String BOUGHT = "Bought";

  private final JLabel BackdoorAction = new JLabel();
  private final JTextField BackDoorInput = new JTextField();
  private final JTextArea BackdoorOutput = new JTextArea();

  private final JLabel CashierAction = new JLabel();
  private final JTextField CashierInput = new JTextField();
  private final JTextArea CashierOutput = new JTextArea();

  private final JScrollPane CashierSP = new JScrollPane();
  private final JScrollPane BackdoorSP = new JScrollPane();

  private static JButton theBtCheck = new JButton(CHECK);
  private static JButton theBtBuy = new JButton(BUY);
  private static JButton theBtBought = new JButton(BOUGHT);
  private StockReadWriter theStock = null;
  private OrderProcessing theOrder = null;

  private ShopController cont = null;

  private static final String RESTOCK = "Add";
  private static final String CLEAR = "Clear";
  private static final String QUERY = "Query";


  private final JTextField theInputNo = new JTextField();

  private static JButton theBtClear = new JButton(CLEAR);
  private static JButton theBtRStock = new JButton(RESTOCK);
  private static JButton theBtQuery = new JButton(QUERY);


  public static String ButtonList = "theBtCheck";

  public static JButton returnShopBtClear() {
    return theBtClear;
  }
  public static JButton returnBtStock() {
    return theBtRStock;
  }
  public static JButton returnBtQuery() {
    return theBtQuery;
  }

  public static JButton returnShopBtCheck() {
    return theBtCheck;
  }
  public static JButton returnBtBought() {
    return theBtBought;
  }
  public static JButton returnBtBuy() {
    return theBtBuy;
  }

  /**
   * Construct the view
   *
   * @param rpc Window in which to construct
   * @param mf  Factor to deliver order and stock objects
   * @param x   x-coordinate of position of window on screen
   * @param y   y-coordinate of position of window on screen
   */



  public ShopView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {

    try                                           //
    {
      theStock = mf.makeStockReadWriter();        // Database access
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
    }
    Container cp = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    cp.setLayout(null);                             // No layout manager
    rootWindow.setSize(W, H);                     // Size of Window
    rootWindow.setLocation(x, y);

    Font f = new Font("Monospaced", Font.PLAIN, 14);  // Font f is

    theBtCheck.setBounds(390, 25 + 60 * 0, 80, 40);    // Check Button
    theBtCheck.addActionListener(                   // Call back code
            e -> cont.doCheck(CashierInput.getText()));
    cp.add(theBtCheck);                           //  Add to canvas

    theBtBuy.setBounds(390, 25 + 60 * 1, 80, 40);      // Buy button
    theBtBuy.addActionListener(                     // Call back code
            e -> cont.doBuy());
    cp.add(theBtBuy);                             //  Add to canvas


    theBtBought.setBounds(390, 25 + 60 * 3, 80, 40);   // Clear Button
    theBtBought.addActionListener(                  // Call back code
            e -> cont.doBought());
    cp.add(theBtBought);                          //  Add to canvas


    CashierAction.setBounds(110, 25, 270, 20);       // Message area
    CashierAction.setText("");                        // Blank
    cp.add(CashierAction);                            //  Add to canvas

    CashierInput.setBounds(110, 50, 270, 40);         // Input Area
    CashierInput.setText("");                           // Blank
    cp.add(CashierInput);                             //  Add to canvas



    CashierSP.setBounds(110, 100, 270, 160);          // Scrolling pane
    CashierOutput.setText("");                        //  Blank
    CashierOutput.setFont(f);                         //  Uses font
    cp.add(CashierSP);                                //  Add to canvas
    CashierSP.getViewport().add(CashierOutput);           //  In TextArea
    rootWindow.setVisible(true);                  // Make visible
    CashierInput.requestFocus();                        // Focus is here


    // BACKDOOR


    theBtQuery.setBounds(17, 25 + 60 * 0, 80, 40);    // Query button
    theBtQuery.addActionListener(                   // Call back code
            e -> cont.doQuery(BackDoorInput.getText()));
    cp.add(theBtQuery);                           //  Add to canvas







    theBtRStock.setBounds(17, 25 + 60 * 1, 80, 40);   // Check Button
    theBtRStock.addActionListener(                  // Call back code
            e -> cont.doRStock(BackDoorInput.getText(),
                    theInputNo.getText()));
    cp.add(theBtRStock);                          //  Add to canvas


    theBtClear.setBounds(17, 25 + 60 * 2, 80, 40);    // Clear button
    theBtClear.addActionListener(                   // Call back code
            e -> cont.doClear());
    cp.add(theBtClear);                           //  Add to canvas


    BackdoorAction.setBounds(110, 300, 270, 20);       // Message area
    BackdoorAction.setText("");                        // Blank
    cp.add(BackdoorAction);                            //  Add to canvas

    BackDoorInput.setBounds(110, 275, 120, 40);         // Input Area
    BackDoorInput.setText("");                           // Blank
    cp.add(BackDoorInput);                             //  Add to canvas


    theInputNo.setBounds(260, 275, 120, 40);       // Input Area
    theInputNo.setText("0");                        // 0
    cp.add(theInputNo);                           //  Add to canvas

  }


  /**
   * The controller object, used so that an interaction can be passed to the controller
   *
   * @param c The controller
   */

  public void setController(ShopController c) {
    cont = c;
  }

  /**
   * Update the view
   *
   * @param modelC The observed model
   * @param arg    Specific args
   */
  @Override
  public void update(Observable modelC, Object arg) {
    ShopModel model = (ShopModel) modelC;
    String message = (String) arg;
    CashierAction.setText(message);



    Basket basket = model.getBasket();
    if (basket == null)
      CashierOutput.setText("Customers order");
    else
      CashierOutput.setText(basket.getDetails());

    CashierInput.requestFocus();               // Focus is here
  }


}



