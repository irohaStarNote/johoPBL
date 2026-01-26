package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;

import controller.AppController;
import model.ExpenseItem;
import model.ExpenseModel;

public class InputView extends JFrame {

    private ExpenseModel model;
    private AppController ctrl;

    private final Color COLOR_PRIMARY = new Color(52, 152, 219);
    private final Color COLOR_DARK = new Color(44, 62, 80);
    private final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private final Color COLOR_BG = new Color(245, 246, 250);

    private class Row {
        JCheckBox check;
        JLabel nameField;
        JTextField amountField;
    }
private ArrayList<Row> rows;

    private JLabel totalLabel;
    private JLabel incomeLabel;
    private JLabel remainLabel;
    private JPanel mainPanel;

    public InputView(AppController ctrl, ExpenseModel model) {
        this.ctrl = ctrl;
        this.model = model;

        setTitle("生活費シミュレーション - 入力");
        setSize(950, 700);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setBackground(COLOR_BG);

        rows = new ArrayList<>();        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        for (ExpenseItem item : model.getItems()) {
            addRowUI(mainPanel, item);
        }

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(COLOR_DARK);
        footerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionBtns.setOpaque(false);

        JButton detailBtn = createStyledButton("詳細・分析グラフを表示", COLOR_PRIMARY, Color.WHITE);

        detailBtn.addActionListener(e -> ctrl.onShowDetail());

        actionBtns.add(detailBtn);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        infoPanel.setOpaque(false);

        incomeLabel = createStatusLabel("月収: ￥0");
        totalLabel = createStatusLabel("支出合計: ￥0");
        remainLabel = createStatusLabel("残額: ￥0");
        remainLabel.setForeground(COLOR_SUCCESS);

        infoPanel.add(incomeLabel);
        infoPanel.add(totalLabel);
        infoPanel.add(remainLabel);

        footerPanel.add(actionBtns, BorderLayout.WEST);
        footerPanel.add(infoPanel, BorderLayout.EAST);

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        navBar.setBackground(Color.WHITE);

        JButton backTitleBtn = new JButton("◀ タイトルへ");
        JButton backIncomeBtn = new JButton("◀ 収入入力へ");
        styleNavButton(backTitleBtn);
        styleNavButton(backIncomeBtn);

        backTitleBtn.addActionListener(e -> ctrl.backToTitleView());
        backIncomeBtn.addActionListener(e -> ctrl.backToIncomeView());

        navBar.add(backTitleBtn);
        navBar.add(backIncomeBtn);

        setLayout(new BorderLayout());
        add(navBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        updateTotal();
    }

    public void addItemRow() {
        model.addCustomItem();
        ExpenseItem last = model.getItems().get(model.getItems().size() - 1);
        addRowUI(mainPanel, last);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void refreshUIFromModel() {

        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            ExpenseItem it = model.getItems().get(i);

            r.check.setSelected(it.isChecked());
            r.amountField.setText(String.valueOf(it.getAmount()));
        }

        updateTotal();
    }

    private void addRowUI(JPanel parent, ExpenseItem item) {
        Row row = new Row();
        JPanel line = createRowContainer();

        row.check = new JCheckBox();
        row.check.setSelected(item.isChecked());
        row.check.setOpaque(false);

        row.nameField = new JLabel(item.getName());
        row.nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        row.nameField.setPreferredSize(new Dimension(150, 30));

        row.amountField = new JTextField(String.valueOf(item.getAmount()), 10);
        styleTextField(row.amountField);

        row.check.addActionListener(e -> {
            item.setChecked(row.check.isSelected());
            updateTotal();
            ctrl.saveData();
        });

        row.amountField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { rt(); }
            public void removeUpdate(DocumentEvent e) { rt(); }
            public void changedUpdate(DocumentEvent e) { rt(); }
            private void rt() {
                try {
                    item.setAmount(Integer.parseInt(row.amountField.getText()));
                } catch (NumberFormatException ex) {
                    item.setAmount(0);
                }
                item.setChecked(row.check.isSelected());
                updateTotal();
                ctrl.saveData();
            }
        });

        line.add(row.check);
        line.add(row.nameField);
        line.add(new JLabel("￥"));
        line.add(row.amountField);

        parent.add(line);
        rows.add(row);
    }

    private JPanel createRowContainer() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(1000, 50));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 241)));
        return panel;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Monospaced", Font.PLAIN, 14));
        tf.setMargin(new Insets(2, 5, 2, 5));
    }

    private void styleNavButton(JButton btn) {
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(Color.GRAY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 15, 8, 15));
        return btn;
    }

    private JLabel createStatusLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 16));
        l.setForeground(Color.WHITE);
        return l;
    }

    public void updateTotal() {

        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            ExpenseItem it = model.getItems().get(i);

            it.setChecked(r.check.isSelected());
            try {
                it.setAmount(Integer.parseInt(r.amountField.getText()));
            } catch (NumberFormatException e) {
                it.setAmount(0);
            }
        }

        model.recalculateTotal();

        incomeLabel.setText("月収: ￥" + String.format("%,d", model.getIncome()));
        totalLabel.setText("支出合計: ￥" + String.format("%,d", model.getTotal()));

        int remain = model.getIncome() - model.getTotal();
        remainLabel.setText("残額: ￥" + String.format("%,d", remain));
        remainLabel.setForeground(remain < 0 ? new Color(231, 76, 60) : COLOR_SUCCESS);
    }
}