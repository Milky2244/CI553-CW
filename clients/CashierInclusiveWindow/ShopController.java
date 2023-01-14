package clients.CashierInclusiveWindow;


/**
 * The Cashier Controller
 * @author M A Smith (c) June 2014
 */

public class ShopController
{
  private ShopModel model = null;
  private ShopView view  = null;

  /**
   * Constructor
   * @param model The model 
   * @param view  The view from which the interaction came
   */
  public ShopController(ShopModel model, ShopView view )
  {
    this.view  = view;
    this.model = model;
  }

  /**
   * Check interaction from view
   * @param pn The product number to be checked
   */
  public void doCheck( String pn )
  {
    model.doCheck(pn);
  }

   /**
   * Buy interaction from view
   */
  public void doBuy()
  {
    model.doBuy();
  }
  
   /**
   * Bought interaction from view
   */
  public void doBought()
  {
    model.doBought();
  }
  public void doQuery( String pn )
  {
    model.doQuery(pn);
  }

  /**
   * RStock interaction from view
   * @param pn       The product number to be re-stocked
   * @param quantity The quantity to be re-stocked
   */
  public void doRStock( String pn, String quantity )
  {
    model.doRStock(pn, quantity);
  }

  /**
   * Clear interaction from view
   */
  public void doClear()
  {
    model.doClear();
  }
}
