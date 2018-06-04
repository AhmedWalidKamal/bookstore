package controller;

import service.BackendServices;

public class Home {

    private BackendServices sys;
    private MainController mainController;

    public void setBackEndService(BackendServices sys) {
        this.sys = sys;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
