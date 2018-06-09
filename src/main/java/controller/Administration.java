package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

class Administration {

    @FXML
    private JFXTextField isbn, title, authors, publisher, price, threshold, publicationYear;

    @FXML
    private JFXComboBox<Label> category;

    @FXML
    private JFXButton addBookButton, clearButton;

    @FXML
    private Label failLabel;

    @FXML
    private AnchorPane rootPane;

    private Node node;

    Administration() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/administration.fxml"));
        fxmlLoader.setController(this);
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    Node getNode() {
        return node;
    }

    private void init() {
        clearButton.setOnMouseClicked(mouseEvent -> handleClearButton());
        addBookButton.setOnMouseClicked(mouseEvent -> handleAddBook());
        initFields();
    }

    private void initFields() {
        initCategory();
        initISBN();
        initTitle();
        initAuthors();
        initPublisher();
        initPrice();
        initThreshold();
        initPublicationYear();
    }

    private void initPublicationYear() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Publication Year Required");
        publicationYear.getValidators().add(validator);
        publicationYear.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) publicationYear.validate();
        });
    }

    private void initThreshold() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Threshold Required");
        threshold.getValidators().add(validator);
        threshold.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) threshold.validate();
        });
    }

    private void initPrice() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Price Required");
        price.getValidators().add(validator);
        price.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) price.validate();
        });
    }

    private void initPublisher() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Publisher Required");
        publisher.getValidators().add(validator);
        publisher.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) publisher.validate();
        });
    }

    private void initAuthors() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Author(s) Required");
        authors.getValidators().add(validator);
        authors.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) authors.validate();
        });
    }

    private void initTitle() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Title Required");
        title.getValidators().add(validator);
        title.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) title.validate();
        });
    }

    private void initCategory() {
        category.getItems().add(new Label("Science"));
        category.getItems().add(new Label("Art"));
        category.getItems().add(new Label("Religion"));
        category.getItems().add(new Label("History"));
        category.getItems().add(new Label("Geography"));
    }

    private void initISBN() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("ISBN Required");
        isbn.getValidators().add(validator);
        isbn.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) isbn.validate();
        });
    }

    private void clearFields() {
        isbn.clear();
        title.clear();
        authors.clear();
        publisher.clear();
        price.clear();
        publicationYear.clear();
        threshold.clear();
    }

    private void handleClearButton() {
        JFXSnackbar bar = new JFXSnackbar(rootPane);
        bar.enqueue(new JFXSnackbar.SnackbarEvent("Fields Cleared"));
        failLabel.setVisible(false);
        clearFields();
    }

    private void handleAddBook() {
        if (validFields()) {
            try {
                boolean success = MainController.getInstance().getBackendService().
                        addBook(isbn.getText(), publisher.getText(),
                                title.getText(), Integer.parseInt(threshold.getText()),
                                getDate(publicationYear.getText()), Float.parseFloat(price.getText()),
                                category.getValue().getText(), getAuthorsList(authors.getText()));
                if (success) {
                    JFXSnackbar bar = new JFXSnackbar(rootPane);
                    bar.enqueue(new JFXSnackbar.SnackbarEvent("Book Added Successfully"));
                    failLabel.setVisible(false);
                    clearFields();
                } else {
                    failLabel.setText("Failed to add book!");
                    failLabel.setVisible(true);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                failLabel.setText("Failed to add book!");
                failLabel.setVisible(true);
            }
        } else {
            failLabel.setVisible(true);
        }
    }

    private Date getDate(String pubYearString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = null;
        try {
            date = sdf.parse(pubYearString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private Collection<String> getAuthorsList(String authorNames) {
        return new ArrayList<>(Arrays.asList(authorNames.split(",")));
    }


    private boolean validFields() {
        return validateIsbn() && validateTitle() && validateAuthors() && validatePublisher() && validatePrice()
                && validateThreshold() && validatePublYear() && validateCategory();
    }

    private boolean validateCategory() {
        if (category.getValue() == null) {
            failLabel.setText("Invalid Category");
            return false;
        }
        return true;
    }

    private boolean validatePublYear() {
        if (publicationYear.getText().isEmpty()) {
            failLabel.setText("Invalid publication year");
            return false;
        }
        try {
            Integer.parseInt(publicationYear.getText());
            return true;
        } catch (NumberFormatException e) {
            failLabel.setText("Invalid publication year");
            return false;
        }
    }

    private boolean validateThreshold() {
        if (threshold.getText().isEmpty()) {
            failLabel.setText("Invalid threshold");
            return false;
        }
        try {
            Integer.parseInt(threshold.getText());
            return true;
        } catch (NumberFormatException e) {
            failLabel.setText("Invalid threshold");
            return false;
        }
    }

    private boolean validatePrice() {
        if (price.getText().isEmpty()) {
            failLabel.setText("Invalid price");
            return false;
        }
        try {
            Float.parseFloat(price.getText());
            return true;
        }
        catch (NumberFormatException e) {
            failLabel.setText("Invalid price");
            return false;
        }
    }

    private boolean validatePublisher() {
        if (publisher.getText().isEmpty()) {
            failLabel.setText("Invalid Publisher");
            return false;
        }
        return true;
    }

    private boolean validateAuthors() {
        if (authors.getText().isEmpty()) {
            failLabel.setText("Invalid Authors");
            return false;
        }
        return true;
    }

    private boolean validateTitle() {
        if (title.getText().isEmpty()) {
            failLabel.setText("Invalid Title");
            return false;
        }
        return true;
    }

    private boolean validateIsbn() {
        if (isbn.getText().length() != 13 || !isbn.getText().matches("[0-9]+")) {
            failLabel.setText("Invalid ISBN");
            return false;
        }
        return true;
    }
}
