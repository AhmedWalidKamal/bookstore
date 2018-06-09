package controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.Book;
import model.BookOrder;

import java.sql.SQLException;

public class BookOrders {

    private static final int MAX_ORDERS_PER_PAGE = 5;

    @FXML
    private AnchorPane ordersRootPane;

    private JFXTreeTableView<TableBookOrder> ordersTable;

    @FXML
    private JFXButton placeOrder, refresh;

    @FXML
    private Pagination pagination;

    private JFXTreeTableColumn<TableBookOrder, String> publisher, isbn;
    private JFXTreeTableColumn<TableBookOrder, Number> quantity, orderNumber;
    private JFXTreeTableColumn<TableBookOrder, Boolean> confirm;
    private ObservableList<TableBookOrder> orders;

    @FXML
    public void initialize() {
        this.orders = FXCollections.observableArrayList();
        initTable();
        try {
            initPagination();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshAction() {
        loadPage(pagination.getCurrentPageIndex());
    }

    @FXML
    private void placeOrderAction() {

    }

    private void initTable() {
        this.ordersTable = new JFXTreeTableView<>();
        initColumns();
        ordersTable.getColumns().setAll(orderNumber, isbn, publisher, quantity, confirm);
        ordersTable.setColumnResizePolicy(JFXTreeTableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.setShowRoot(false);
    }

    private void initPagination() throws SQLException {
        pagination.setPageCount(MainController.getInstance().getBackendService().getOrdersPageCount(MAX_ORDERS_PER_PAGE));
        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex);
        return ordersTable;
    }

    private void loadPage(int pageIndex) {
        try {
            loadOrders(pageIndex);
            pagination.setPageCount(MainController.getInstance().getBackendService().getOrdersPageCount(MAX_ORDERS_PER_PAGE));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load orders from database");
        }
        buildTable();
    }

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
        initConfirmCol();
    }

    private void initConfirmCol() {
        confirm = new JFXTreeTableColumn<>("Confirm");
        confirm.setPrefWidth(250);
        confirm.setSortable(false);
        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        confirm.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<TableBookOrder, Boolean>, ObservableValue<Boolean>>() {
            @Override public ObservableValue<Boolean> call(JFXTreeTableColumn.CellDataFeatures<TableBookOrder, Boolean> features) {
                return new SimpleBooleanProperty(features.getValue() != null);
            }
        });

        // create a cell value factory with an add button for each row in the table.
        confirm.setCellFactory(new Callback<TreeTableColumn<TableBookOrder, Boolean>, TreeTableCell<TableBookOrder, Boolean>>() {
            @Override public JFXTreeTableCell<TableBookOrder, Boolean> call(TreeTableColumn<TableBookOrder, Boolean> personBooleanTableColumn) {
                return new ConfirmButtonCell();
            }
        });
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

        private IntegerProperty orderNo;
        private IntegerProperty quantity;

        TableBookOrder(BookOrder bookOrder) {
            this.ISBN = new SimpleStringProperty(bookOrder.getISBN());
            this.publisher = new SimpleStringProperty(bookOrder.getPublisherName());
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

        public int getOrderNo() {
            return orderNo.get();
        }

        IntegerProperty quantityProperty() {
            return quantity;
        }
    }

    private class ConfirmButtonCell extends JFXTreeTableCell<TableBookOrder, Boolean> {
        final JFXButton confirmButton = new JFXButton("Confirm");
        final StackPane paddedButton = new StackPane();

        ConfirmButtonCell() {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(confirmButton);
            confirmButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    System.out.println(getTreeTableRow().getTreeItem().getValue().getOrderNo());
                    try {
                        boolean success = MainController.getInstance().getBackendService().
                                confirmOrder(getTreeTableRow().getTreeItem().getValue().getOrderNo());
                        if (success) {
                            TreeItem<TableBookOrder> item = getTreeTableRow().getTreeItem();
                            item.getParent().getChildren().remove(item);
                            pagination.setPageCount(MainController.getInstance().getBackendService().getOrdersPageCount(MAX_ORDERS_PER_PAGE));
                            JFXSnackbar bar = new JFXSnackbar(ordersRootPane);
                            bar.enqueue(new JFXSnackbar.SnackbarEvent("Order #" + getTreeTableRow().
                                    getTreeItem().getValue().getOrderNo() + " confirmed successfully"));
                        } else {
                            System.out.println("Couldn't confirm order number: " + getTreeTableRow().
                                    getTreeItem().getValue().getOrderNo());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Couldn't confirm order number: " + getTreeTableRow().getTreeItem().
                                getValue().getOrderNo());
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
