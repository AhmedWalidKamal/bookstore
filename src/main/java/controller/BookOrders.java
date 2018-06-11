package controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import model.Book;

import model.BookOrder;

import java.sql.SQLException;

public class BookOrders {

    private static final int MAX_ORDERS_PER_PAGE = 5;

    @FXML
    private StackPane ordersRootPane;

    private JFXTreeTableView<TableBookOrder> ordersTable;

    @FXML
    private JFXButton placeOrder, refresh, issueOrderBtn;

    @FXML
    private Pagination pagination;

    @FXML
    private JFXDialogLayout dialogLayout;

    @FXML
    private JFXDialog dialog;

    @FXML
    private JFXTextField quantityTextField, isbnTextField;

    @FXML
    private Label errorLabel;

    private JFXTreeTableColumn<TableBookOrder, String> publisher, isbn, orderDate;
    private JFXTreeTableColumn<TableBookOrder, Number> quantity, orderNumber;
    private JFXTreeTableColumn<TableBookOrder, Boolean> confirm;
    private ObservableList<TableBookOrder> orders;

    @FXML
    public void initialize() {
        this.orders = FXCollections.observableArrayList();
        initTable();
        initDialog();
        try {
            initPagination();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initDialog() {
        dialog.setDialogContainer(ordersRootPane);
        dialog.setContent(dialogLayout);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.onDialogClosedProperty();
        dialogLayout.setPrefWidth(800);
        dialogLayout.setPrefHeight(500);
        initFields();
        dialog.setOnDialogClosed(jfxDialogEvent -> {
            errorLabel.setVisible(false);
            clearFields();
        });
    }

    private void initFields() {
        initISBNField();
        initQuantTextField();
    }

    private void initQuantTextField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Quantity Required");
        quantityTextField.getValidators().add(validator);
        quantityTextField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) quantityTextField.validate();
        });
    }

    private void initISBNField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("ISBN Required");
        isbnTextField.getValidators().add(validator);
        isbnTextField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) isbnTextField.validate();
        });
    }

    @FXML
    private void refreshAction() {
        try {
            loadPage(pagination.getCurrentPageIndex());
            JFXSnackbar bar = new JFXSnackbar(ordersRootPane);
            bar.enqueue(new JFXSnackbar.SnackbarEvent("Table refreshed successfully!"));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load orders from database");
        }
    }

    @FXML
    private void placeOrderAction() {
        dialog.show();
    }

    @FXML
    private void issueOrderAction() {
        if (validateFields()) {
            try {
                System.out.println("Issuing order");
                int orderNumber = MainController.getInstance().getBackendService().orderBook(isbnTextField.getText(), Integer.parseInt(quantityTextField.getText()));
                if (orderNumber != -1) {
                    clearFields();
                    dialog.close();
                    errorLabel.setVisible(false);
                    loadPage(pagination.getCurrentPageIndex());
                    JFXSnackbar bar = new JFXSnackbar(ordersRootPane);
                    bar.enqueue(new JFXSnackbar.SnackbarEvent("Order placed successfully!"));
                } else {
                    errorLabel.setText("Failed to issue order");
                    errorLabel.setVisible(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                errorLabel.setText("Failed to issue order");
                errorLabel.setVisible(true);
            }
        } else {
            errorLabel.setVisible(true);
        }
    }

    private void clearFields() {
        isbnTextField.clear();
        quantityTextField.clear();
    }

    private boolean validateFields() {
        return validateISBN() && validateQuantity();
    }

    private boolean validateISBN() {
        if (isbnTextField.getText().length() != 13 || !isbnTextField.getText().matches("[0-9]+")) {
            errorLabel.setText("Invalid ISBN");
            return false;
        }
        return true;
    }

    private boolean validateQuantity() {
        if (quantityTextField.getText().isEmpty()) {
            errorLabel.setText("Quantity Required");
            return false;
        }
        try {
            Integer.parseInt(quantityTextField.getText());
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid quantity");
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void initTable() {
        this.ordersTable = new JFXTreeTableView<>();
        initColumns();
        ordersTable.getColumns().setAll(orderNumber, isbn, publisher, quantity, orderDate,confirm);
        ordersTable.setColumnResizePolicy(JFXTreeTableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.setShowRoot(false);
    }

    private void initPagination() throws SQLException {
        pagination.setPageCount(MainController.getInstance().getBackendService().getOrdersPageCount(MAX_ORDERS_PER_PAGE));
        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        try {
            loadPage(pageIndex);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load orders from database");
        }
        return ordersTable;
    }

    private void loadPage(int pageIndex) throws SQLException {
        loadOrders(pageIndex);
        pagination.setPageCount(MainController.getInstance().getBackendService().getOrdersPageCount(MAX_ORDERS_PER_PAGE));
        buildTable();
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {

        final TreeItem<TableBookOrder> root = new RecursiveTreeItem<>(this.orders, RecursiveTreeObject::getChildren);
        ordersTable.setRoot(root);
    }

    private void loadOrders(int pageIndex) throws SQLException {
        this.orders.clear();
        for (BookOrder bookOrder : MainController.getInstance().getBackendService().getOrders(pageIndex + 1, BookOrders.MAX_ORDERS_PER_PAGE)) {
            this.orders.add(new TableBookOrder(bookOrder));
        }
    }

    private void initColumns() {
        initOrdersNumberCol();
        initISBNCol();
        initPublisherCol();
        initQuantityCol();
        initOrderDateCol();
        initConfirmCol();
    }

    private void initOrderDateCol() {
        orderDate = new JFXTreeTableColumn<>("Order Date");
        orderDate.setPrefWidth(300);
        orderDate.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBookOrder, String> param) -> {
            if(orderDate.validateValue(param)) return param.getValue().getValue().orderDateProperty();
            else return orderDate.getComputedValue(param);
        });
    }

    private void initConfirmCol() {
        confirm = new JFXTreeTableColumn<>("Confirm");
        confirm.setPrefWidth(250);
        confirm.setSortable(false);
        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        confirm.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));

        // create a cell value factory with an add button for each row in the table.
        confirm.setCellFactory(personBooleanTableColumn -> new ConfirmButtonCell());
    }

    private void initPublisherCol() {
        publisher = new JFXTreeTableColumn<>("Publisher");
        publisher.setPrefWidth(335);
        publisher.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBookOrder, String> param) -> {
            if(publisher.validateValue(param)) return param.getValue().getValue().publisherProperty();
            else return publisher.getComputedValue(param);
        });
    }

    private void initQuantityCol() {
        quantity = new JFXTreeTableColumn<>("Quantity");
        quantity.setPrefWidth(260);
        quantity.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBookOrder, Number> param) ->{
            if(quantity.validateValue(param)) return param.getValue().getValue().quantityProperty();
            else return quantity.getComputedValue(param);
        });
    }

    private void initISBNCol() {
        isbn = new JFXTreeTableColumn<>("ISBN");
        isbn.setPrefWidth(310);
        isbn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBookOrder, String> param) ->{
            if(isbn.validateValue(param)) return param.getValue().getValue().ISBNProperty();
            else return isbn.getComputedValue(param);
        });
    }

    private void initOrdersNumberCol() {
        orderNumber = new JFXTreeTableColumn<>("Order Number");
        orderNumber.setPrefWidth(135);
        orderNumber.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBookOrder, Number> param) ->{
            if(orderNumber.validateValue(param)) return param.getValue().getValue().orderNoProperty();
            else return orderNumber.getComputedValue(param);
        });
    }

    class TableBookOrder extends RecursiveTreeObject<TableBookOrder> {
        private StringProperty ISBN;
        private StringProperty publisher;
        private StringProperty orderDate;

        private IntegerProperty orderNo;
        private IntegerProperty quantity;

        TableBookOrder(BookOrder bookOrder) {
            this.ISBN = new SimpleStringProperty(bookOrder.getISBN());
            this.publisher = new SimpleStringProperty(bookOrder.getPublisherName());
            this.orderDate = new SimpleStringProperty(bookOrder.getOrderDate().toString());
            this.orderNo = new SimpleIntegerProperty(bookOrder.getOrderNo());
            this.quantity = new SimpleIntegerProperty(bookOrder.getQuantity());
        }

        StringProperty ISBNProperty() {
            return ISBN;
        }

        StringProperty publisherProperty() {
            return publisher;
        }

        IntegerProperty orderNoProperty() {
            return orderNo;
        }

        int getOrderNo() {
            return orderNo.get();
        }

        IntegerProperty quantityProperty() {
            return quantity;
        }

        public String getOrderDate() {
            return orderDate.get();
        }

        public StringProperty orderDateProperty() {
            return orderDate;
        }
    }

    private class ConfirmButtonCell extends JFXTreeTableCell<TableBookOrder, Boolean> {
        final JFXButton confirmButton = new JFXButton("Confirm");
        final StackPane paddedButton = new StackPane();

        ConfirmButtonCell() {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(confirmButton);
            confirmButton.getStyleClass().add("blue-btn");
            confirmButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    int ordNo = getTreeTableRow().getTreeItem().getValue().getOrderNo();
                    System.out.println(ordNo);
                    try {
                        boolean success = MainController.getInstance().getBackendService().
                                confirmOrder(ordNo);
                        if (success) {
                            TreeItem<TableBookOrder> item = getTreeTableRow().getTreeItem();
                            item.getParent().getChildren().remove(item);
                            loadPage(pagination.getCurrentPageIndex());
                            JFXSnackbar bar = new JFXSnackbar(ordersRootPane);
                            bar.enqueue(new JFXSnackbar.SnackbarEvent("Order #" + ordNo
                                    + " confirmed successfully"));
                        } else {
                            System.out.println("Couldn't confirm order number: " + ordNo);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Couldn't confirm order number: " + ordNo);

                    }
                }
            });
        }

        // Places confirm button if field isn't empty
        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }
}
