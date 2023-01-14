package clients.CashierInclusiveWindow;

import middle.MiddleFactory;
import middle.Names;
import middle.RemoteMiddleFactory;

import javax.swing.*;





public class ShopClient
{
   public static void main (String args[])
   {
     String stockURL = args.length < 1     // URL of stock RW
                     ? Names.STOCK_RW      //  default  location
                     : args[0];            //  supplied location
     String orderURL = args.length < 2     // URL of order
                     ? Names.ORDER         //  default  location
                     : args[1];            //  supplied location
     
    RemoteMiddleFactory mrf = new RemoteMiddleFactory();
    mrf.setStockRWInfo( stockURL );
    mrf.setOrderInfo  ( orderURL );        //
    displayGUI(mrf);                       // Create GUI
  }


  private static void displayGUI(MiddleFactory mf)
  {     
    JFrame  window = new JFrame();


    window.setTitle( "Shop Client (MVC RMI)");
    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    
    ShopModel model = new ShopModel(mf);
    ShopView view  = new ShopView( window, mf, 0, 0 );
    ShopController cont  = new ShopController( model, view );
    view.setController( cont );

    model.addObserver( view );       // Add observer to the model
    window.setVisible(true);         // Display Screen
    model.askForUpdate();
  }
}
