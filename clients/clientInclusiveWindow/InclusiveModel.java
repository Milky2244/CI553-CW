package clients.clientInclusiveWindow;

import catalogue.Basket;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderException;
import middle.OrderProcessing;
import middle.StockReadWriter;

import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;


public class InclusiveModel extends Observable {
    private String PickAction = "";
    private String PickOutput = "";
    private OrderProcessing theOrder = null;
    private String CollectionAction = "";
    private String CollectionOutput = "";
    private AtomicReference<Basket> theBasket = new AtomicReference<>();
    private StateOf worker = new StateOf();
    private StockReadWriter theStock = null;

    /*
     * Construct the model of the Collection client
     * @param mf The factory to create the connection objects
     */
    public InclusiveModel(MiddleFactory mf) {
        try                                           //
        {
            theStock = mf.makeStockReadWriter();  // Database access
            theOrder = mf.makeOrderProcessing();        // Process order
        } catch (Exception e) {
            DEBUG.error("%s\n%s",
                    "CollectModel.constructor\n%s",
                    e.getMessage());
        }
        theBasket.set(null);                  // Initial Basket
        // Start a background check to see when a new order can be picked
        new Thread(() -> checkForNewOrder()).start();
    }

    private void checkForNewOrder() {
        while (true) {
            try {
                boolean isFree = worker.claim();     // Are we free
                if (isFree)                        // T
                {                                    //
                    Basket sb =
                            theOrder.getOrderToPick();       //  Order
                    if (sb != null)                  //  Order to pick
                    {                                  //  T
                        theBasket.set(sb);               //   Working on
                        PickAction = "Order to pick";     //   what to do
                    } else {                           //  F
                        worker.free();                   //  Free
                        PickAction = "";                  //
                    }
                    setChanged();
                    notifyObservers(PickAction);
                }                                    //
                Thread.sleep(2000);                  // idle
            } catch (Exception e) {
                DEBUG.error("%s\n%s",                // Eek!
                        "BackGroundCheck.run()\n%s",
                        e.getMessage());
            }
        }
    }


    /**
     * Check if the product is in Stock
     *
     * @param orderNumber The order to be collected
     */
    public void doCollect(String orderNumber) {
        int orderNum = 0;
        String on = orderNumber.trim();         // Product no.
        try {
            orderNum = Integer.parseInt(on);       // Convert
        } catch (Exception err) {
            // Convert invalid order number to 0
        }
        try {
            boolean ok =
                    theOrder.informOrderCollected(orderNum);
            if (ok) {
                CollectionAction = "";
                CollectionOutput = "Collected order #" + orderNum;
            } else {
                CollectionAction = "No such order to be collected : " + orderNumber;
                CollectionOutput = "No such order to be collected : " + orderNumber;
            }
        } catch (Exception e) {
            CollectionOutput = String.format("%s\n%s",
                    "Error connection to order processing system",
                    e.getMessage());
            CollectionAction = "!!!Error";
        }
        setChanged();
        notifyObservers(CollectionAction);
    }

    public void doPick() {
                    String PickAction = "";
                    try {
                        Basket basket = theBasket.get();       // Basket being picked
                        if (basket != null)                   // T
                        {
                            theBasket.set(null);                //  Picked
                            int no = basket.getOrderNum();        //  Order no
                            theOrder.informOrderPicked(no);     //  Tell system
                            PickAction = "";                       //  Inform picker
                            worker.free();                        //  Can pick some more
                        } else {
                            PickAction = "No order to pick";       //    Invalid error return
                        }
                        setChanged();
                        notifyObservers(PickAction);
                    } catch (OrderException e)                // Error return
                    {
                        DEBUG.error("InclusiveModel.doPick()\n%s\n",
                                e.getMessage());
                    }
                    setChanged();
                    notifyObservers(PickAction);
                }

    public static class StateOf
    {
        private boolean held = false;

        /**
         * Claim exclusive access
         * @return true if claimed else false
         */
        public synchronized boolean claim()   // Semaphore
        {
            return held ? false : (held = true);
        }

        /**
         * Free the lock
         */
        public synchronized void free()     //
        {
            assert held;
            held = false;
        }

    }
    /**
     * The output to be displayed
     *
     * @return The string to be displayed
     */
    public String getResponce() {
        return PickOutput;
    }

    public String getResponce2() {
        return CollectionOutput;
    }

    public Basket getBasket() {
        return theBasket.get();
    }


    private boolean held = false;

    public synchronized boolean claim()   // Semaphore
    {
        return held ? false : (held = true);
    }

    /**
     * Free the lock
     */
    public synchronized void free()     //
    {
        assert held;
        held = false;
    }




}



