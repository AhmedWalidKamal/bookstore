package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
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
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.BookOrder;

import java.sql.SQLException;

public class BookOrders {

    @FXML
    private AnchorPane ordersRootPane;

    @FXML
    private JFXTreeTableView<TableBookOrder> ordersTable;

    @FXML
    private JFXButton placeOrder;

    private JFXTreeTableColumn<TableBookOrder, String> publisher, isbn;
    private JFXTreeTableColumn<TableBookOrder, Number> quantity, orderNumber;
    private JFXTreeTableColumn<TableBookOrder, Boolean> confirm;

    private ObservableList<TableBookOrder> orders;

    @FXML
    public void initialize() {
        this.orders = FXCollections.observableArrayList();
        initColumns();
        try {
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load orders");
        }
        buildTable();
    }

    private void buildTable() {
        final TreeItem<TableBookOrder> root = new RecursiveTreeItem<>(this.orders, RecursiveTreeObject::getChildren);
        ordersTable.getColumns().setAll(orderNumber, isbn, publisher, quantity, confirm);
        ordersTable.setColumnResizePolicy(JFXTreeTableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.setRoot(root);
        ordersTable.setShowRoot(false);
    }

    private void loadOrders() throws SQLException {
        for (BookOrder bookOrder : MainController.getInstance().getBackendService().getOrders(1, Integer.MAX_VALUE)) {
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

        IntegerProperty quantityProperty() {
            return quantity;
        }
    }

    private class ConfirmButtonCell extends JFXTreeTableCell<TableBookOrder, Boolean> {
        // a button for adding a new person.
        final JFXButton confirmButton = new JFXButton("Confirm");
        // pads and centers the add button in the cell.
        final StackPane paddedButton = new StackPane();

        ConfirmButtonCell() {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(confirmButton);
            confirmButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    // Handle click on button
                }
            });
        }

        /** places an add button in the row only if the row is not empty. */
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
