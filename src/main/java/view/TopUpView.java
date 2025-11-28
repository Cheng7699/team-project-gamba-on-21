package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.topup.TopUpController;
import interface_adapter.topup.TopUpState;
import interface_adapter.topup.TopupViewModel;

import javax.print.MultiDocPrintService;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TopUpView extends JPanel implements ActionListener, PropertyChangeListener {

    public static final String VIEW_NAME = "topup";


    private final TopupViewModel topupViewModel;
    private final ViewManagerModel viewManagerModel;
    private TopUpController topUpController = null;

    private final JLabel topupErrorLabel = new JLabel();
    private final JTextField topupInputField = new  JTextField(20);
    private final JButton topup;
    private final JButton cancel;

    public TopUpView(TopupViewModel topupViewModel, ViewManagerModel viewManagerModel) {
        this.topupViewModel = topupViewModel;
        this.viewManagerModel = viewManagerModel;
        topupViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel(TopupViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final LabelTextPanel topupInfo = new LabelTextPanel(
                new JLabel(TopupViewModel.TOPUP_LABEL), topupInputField
        );

        topupErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JPanel buttons = new JPanel();
        topup = new JButton(TopupViewModel.TOPUP_BUTTON_LABEL);
        buttons.add(topup);
        cancel = new JButton(TopupViewModel.CANCEL_BUTTON_LABEL);
        buttons.add(cancel);

        topup.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt){
                        if (evt.getSource().equals(topup)){
                            final TopUpState currentState = topupViewModel.getState();
                            if (currentState.getTopupAmount() !="") {
                                topupErrorLabel.setText("");
                                System.out.println("topupcontoller executed with:" + currentState.getUsername() + currentState.getTopupAmount());

                                topUpController.execute(
                                        currentState.getUsername(),
                                        currentState.getTopupAmount()
                                );
                            }
                        }
                    }

                }

        );

        cancel.addActionListener(this);

        topupInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void documentListenerHelper() {
                final TopUpState currentState = topupViewModel.getState();
                currentState.setTopupAmount(topupInputField.getText());
                topupViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });

        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(Box.createVerticalStrut(10));
        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(topupInfo);
        content.add(Box.createVerticalStrut(20));
        content.add(topupErrorLabel);

        add(content, BorderLayout.NORTH);
        add(buttons, BorderLayout.SOUTH);



    }

    public void actionPerformed(ActionEvent evt){
        if  (evt.getSource().equals(cancel)){
            topupErrorLabel.setText("");
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final TopUpState state =  (TopUpState) evt.getNewValue();
        topupInputField.setText(state.getTopupAmount());

        if(state.getTopupAmountError() != null){
            topupErrorLabel.setText(state.getTopupAmountError());

            state.setTopupAmountError(null);
            topupViewModel.setState(state);
        }

    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public void setTopupController(TopUpController topupController) {
        this.topUpController = topupController;
    }
}
