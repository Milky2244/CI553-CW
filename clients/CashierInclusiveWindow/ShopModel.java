package clients.CashierInclusiveWindow;

import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import middle.*;

import java.util.Observable;

/**
 * Implements the Model of the cashier client
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */
public class ShopModel extends Observable
{
  private enum State { process, checked }
  private Basket      theBasket  = null;            // Bought items
  private String      pn = "";                      // Product being processed


  private State       theState   = State.process;   // Current state
  private Product     theProduct = null;            // Current product



  private StockReadWriter theStock     = null;
  private OrderProcessing theOrder     = null;

  /**
   * Construct the model of the Cashier
   * @param mf The factory to create the connection objects
   */

  public ShopModel(MiddleFactory mf)
  {
    try                                           // 
    {      
      theStock = mf.makeStockReadWriter();        // Database access
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch ( Exception e )
    {
      DEBUG.error("ShopModel.constructor\n%s", e.getMessage() );
    }
    theState   = State.process;                  // Current state
  }
  
  /**
   * Get the Basket of products
   * @return basket
   */
  public Basket getBasket()
  {
    return theBasket;
  }

  /**
   * Check if the product is in Stock
   * @param productNum The product number
   */
  public void doCheck(String productNum )
  {
    String CashierAction = "";
    theState  = State.process;                  // State process
    pn  = productNum.trim();                    // Product no.
    int    amount  = 1;                         //  & quantity
    try
    {
      if ( theStock.exists( pn ) )              // Stock Exists?
      {                                         // T
        Product pr = theStock.getDetails(pn);   //  Get details
        if ( pr.getQuantity() >= amount )       //  In stock?
        {                                       //  T
          CashierAction =                           //   Display
            String.format( "%s : %7.2f (%2d) ", //
              pr.getDescription(),              //    description
              pr.getPrice(),                    //    price
              pr.getQuantity() );               //    quantity     
          theProduct = pr;                      //   Remember prod.
          theProduct.setQuantity( amount );     //    & quantity
          theState = State.checked;             //   OK await BUY 
        } else {                                //  F
          CashierAction =                           //   Not in Stock
            pr.getDescription() +" not in stock";
        }
      } else {                                  // F Stock exists
        CashierAction =                             //  Unknown
          "Unknown product number " + pn;       //  product no.
      }
    } catch( StockException e )
    {
      DEBUG.error( "%s\n%s", 
            "ShopModel.doCheck", e.getMessage() );
      CashierAction = e.getMessage();
    }
    setChanged(); notifyObservers(CashierAction);
  }

  /**
   * Buy the product
   */
  public void doBuy()
  {
    String CashierAction = "";
    int    amount  = 1;                         //  & quantity
    try
    {
      if ( theState != State.checked )          // Not checked
      {                                         //  with customer
        CashierAction = "Check if OK with customer first";
      } else {
        boolean stockBought =                   // Buy
          theStock.buyStock(                    //  however
            theProduct.getProductNum(),         //  may fail              
            theProduct.getQuantity() );         //
        if ( stockBought )                      // Stock bought
        {                                       // T
          makeBasketIfReq();                    //  new Basket ?
          theBasket.add( theProduct );          //  Add to bought
          CashierAction = "Purchased " +            //    details
                  theProduct.getDescription();  //
        } else {                                // F
          CashierAction = "!!! Not in stock";       //  Now no stock
        }
      }
    } catch( StockException e )
    {
      DEBUG.error( "%s\n%s", 
            "ShopModel.doBuy", e.getMessage() );
      CashierAction = e.getMessage();
    }
    theState = State.process;                   // All Done
    setChanged(); notifyObservers(CashierAction);
  }
  
  /**
   * Customer pays for the contents of the basket
   */
  public void doBought()
  {
    String CashierAction = "";
    int    amount  = 1;                       //  & quantity
    try
    {
      if ( theBasket != null &&
           theBasket.size() >= 1 )            // items > 1
      {                                       // T
        theOrder.newOrder( theBasket );       //  Process order
        theBasket = null;                     //  reset
      }                                       //
      CashierAction = "Next customer";            // New Customer
      theState = State.process;               // All Done
      theBasket = null;
    } catch( OrderException e )
    {
      DEBUG.error( "%s\n%s", 
            "ShopModel.doCancel", e.getMessage() );
      CashierAction = e.getMessage();
    }
    theBasket = null;
    setChanged(); notifyObservers(CashierAction); // Notify
  }

  /**
   * ask for update of view callled at start of day
   * or after system reset
   */
  public void askForUpdate()
  {
    setChanged(); notifyObservers("Welcome");
  }
  public void doClear()
  {
    String BackdoorAction = "";
    theBasket.clear();                        // Clear s. list
    BackdoorAction = "Enter Product Number";       // Set display
    setChanged(); notifyObservers(BackdoorAction);
  }

  /**
   * return an instance of a Basket
   * @return a new instance of a Basket
   */


  public void doRStock(String productNum, String quantity )
  {
    String BackdoorAction = "";
    theBasket = makeBasket();
    pn  = productNum.trim();                    // Product no.
    String pn  = productNum.trim();             // Product no.
    int amount = 0;
    try
    {
      String aQuantity = quantity.trim();
      try
      {
        amount = Integer.parseInt(aQuantity);   // Convert
        if ( amount < 0 )
          throw new NumberFormatException("-ve");
      }
      catch ( Exception err)
      {
        BackdoorAction = "Invalid quantity";
        setChanged(); notifyObservers(BackdoorAction);
        return;
      }

      if ( theStock.exists( pn ) )              // Stock Exists?
      {                                         // T
        theStock.addStock(pn, amount);          //  Re stock
        Product pr = theStock.getDetails(pn);   //  Get details
        theBasket.add(pr);                      //
        BackdoorAction = "";                         // Display
      } else {                                  // F
        BackdoorAction =                             //  Inform Unknown
                "Unknown product number " + pn;       //  product number
      }
    } catch( StockException e )
    {
      BackdoorAction = e.getMessage();
    }
    setChanged(); notifyObservers(BackdoorAction);
  }
  /**
   * make a Basket when required
   */
  private void makeBasketIfReq()
  {
    if ( theBasket == null )
    {
      try
      {
        int uon   = theOrder.uniqueNumber();     // Unique order num.
        theBasket = makeBasket();                //  basket list
        theBasket.setOrderNum( uon );            // Add an order number
      } catch ( OrderException e )
      {
        DEBUG.error( "Comms failure\n" +
                     "ShopModel.makeBasket()\n%s", e.getMessage() );
      }
    }
  }
  public void doQuery(String productNum )
  {
    String BackdoorAction = "";
    pn  = productNum.trim();                    // Product no.
    try
    {                 //  & quantity
      if ( theStock.exists( pn ) )              // Stock Exists?
      {                                         // T
        Product pr = theStock.getDetails( pn ); //  Product
        BackdoorAction =                             //   Display
                String.format( "%s : %7.2f (%2d) ",   //
                        pr.getDescription(),                  //    description
                        pr.getPrice(),                        //    price
                        pr.getQuantity() );                   //    quantity
      } else {                                  //  F
        BackdoorAction =                             //   Inform
                "Unknown product number " + pn;       //  product number
      }
    } catch( StockException e )
    {
      BackdoorAction = e.getMessage();
    }
    setChanged(); notifyObservers(BackdoorAction);
  }

  /**
   * return an instance of a new Basket
   * @return an instance of a new Basket
   */
  protected Basket makeBasket()
  {
    return new Basket();
  }
}
  
