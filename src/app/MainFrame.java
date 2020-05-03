package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Application GUI
 * Accessible only when running a trained model
 */
public class MainFrame extends JFrame
{
    /** The model we are working with */
    private Model model;

    /** Somewhere to put the text to classify */
    private JTextArea textInput;

    /** Label to show the classification result */
    private JLabel outputClassLabel;

    /** Associated with text input */
    private JLabel topDescriptionLabel;

    /** Button to start the classification */
    private JButton classifyButton;

    public MainFrame(Model model)
    {
        this.model = model;

        Container pane = this.getContentPane();
        pane.setLayout(new BorderLayout());
        Container bottomContainer = new Container();
        bottomContainer.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pane.add(bottomContainer, BorderLayout.PAGE_END);

        textInput = new JTextArea();
        pane.add(textInput, BorderLayout.CENTER);

        topDescriptionLabel = new JLabel("Text to classify");
        pane.add(topDescriptionLabel, BorderLayout.PAGE_START);

        classifyButton = new JButton("Classify");
        classifyButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String text = textInput.getText();
                String result = model.classifyText(text);
                outputClassLabel.setText(result);
            }
        });
        bottomContainer.add(classifyButton);

        outputClassLabel = new JLabel();
        bottomContainer.add(outputClassLabel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1280, 720);
        this.setLocationRelativeTo(null);
    }
}
