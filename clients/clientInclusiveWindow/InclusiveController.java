package clients.clientInclusiveWindow;

public class InclusiveController {
        private InclusiveModel model = null;
        private InclusiveView view  = null;

        /**
         * Constructor
         * @param model The model
         * @param view  The view from which the interaction came
         */
  public InclusiveController(InclusiveModel model, InclusiveView view )
        {
            this.view  = view;
            this.model = model;
        }

        /**
         * Collect interaction from view
         * @param orderNum The order collected
         */
        public void doCollect( String orderNum )
        {
            model.doCollect(orderNum);
        }

    public void doPick()
    {
        model.doPick();
    }

}

