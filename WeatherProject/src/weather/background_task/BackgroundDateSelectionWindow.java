package weather.background_task;

import java.awt.Color;
import java.awt.Component;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import weather.common.gui.component.BUDialog;

/**
 * A date selection window specifically for the background task window.
 *
 * @author Colton Daily (2014)
 * @version Spring 2014
 */
public class BackgroundDateSelectionWindow extends BUDialog {

    private DefaultTableModel tableModel;
    private final DefaultTableCellRenderer tableRenderer;
    private int day;
    private int month;
    private int year;
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;

    private static Calendar date;
    private static Calendar resetDate;
    private static boolean dateSelected = false;

    /**
     * Creates new form BackgroundDateSelectionWindow
     */
    private BackgroundDateSelectionWindow(Calendar initialDate) {
        super();
        tableModel = new DefaultTableModel() {
            //make sure they can't edit the cells
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean selected, boolean focused, int row, int column) {
                super.getTableCellRendererComponent(table, value, selected, focused, row, column);
                //weekends pink
                if (column == 0 || column == 6) {
                    setBackground(Color.PINK);
                } else { //weekdays white
                    setBackground(Color.WHITE);
                }
                if (table.isCellSelected(row, column)) { //selected cell 
                    try {
                        selectedDay = (int) calendarTable.getValueAt(calendarTable.getSelectedRow(), calendarTable.getSelectedColumn());
                        setBackground(Color.GRAY); //selected gray
                    } catch (NullPointerException npe) {
                        //blank cell
                    }
                }
                setBorder(null);
                setForeground(Color.BLACK);
                return this;
            }
        };
        initComponents();

        date = initialDate;
        setupCalendarTable();
        super.postInitialize(false);
    }

    /**
     * Displays a new BackgroundDateSelectionWindow and will return the date the
     * user selects.
     *
     * @param initialDate the starting date to look at
     * @return the new selected date
     */
    public static Calendar getNewDate(Calendar initialDate) {
        resetDate = initialDate;
        GregorianCalendar calendar = null;
        if (display(initialDate)) {
            calendar = new GregorianCalendar(date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        } else {
            return initialDate;
        }
        return calendar;
    }

    private static boolean display(Calendar initialDate) {
        new BackgroundDateSelectionWindow(initialDate);

        return dateSelected;
    }

    /**
     * Creates the calendar and adds all the dates to the table.
     */
    private void setupCalendarTable() {
        month = date.get(GregorianCalendar.MONTH);
        year = date.get(GregorianCalendar.YEAR);
        day = date.get(GregorianCalendar.DAY_OF_MONTH);
        selectedMonth = month;
        selectedDay = day;
        selectedYear = year;
        //add the columns
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String d : daysOfWeek) {
            tableModel.addColumn(d);
        }

        //setup table selection rules and size
        calendarTable.getTableHeader().setResizingAllowed(false);
        calendarTable.getTableHeader().setReorderingAllowed(false);
        calendarTable.setColumnSelectionAllowed(true);
        calendarTable.setRowSelectionAllowed(true);
        calendarTable.setCellSelectionEnabled(true);
        calendarTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        calendarTable.setRowHeight(38);
        tableModel.setColumnCount(7);
        tableModel.setRowCount(6);

        updateCalendar(month, year);
    }

    /**
     * Gets the days of the week for the specified month and year.
     *
     * @param month the month the user is looking at.
     * @param year the year the user is looking at.
     */
    private void updateCalendar(int month, int year) {
        String[] months = {"January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November", "December"};
        int totalDays;
        int startOfMonth;

        //update month label
        monthLabel.setText(months[month] + " " + year);

        //set all cells to null
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                tableModel.setValueAt(null, i, j);
            }
        }

        //get the first day of the month and for the year selected
        GregorianCalendar calendar = new GregorianCalendar(year, month, 1);
        totalDays = calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        startOfMonth = calendar.get(GregorianCalendar.DAY_OF_WEEK);

        //update the cells with the days
        int row, column;
        for (int i = 1; i <= totalDays; i++) {
            row = (i + startOfMonth - 2) / 7;
            column = (i + startOfMonth - 2) % 7;
            tableModel.setValueAt(i, row, column);
            if (i == day) {
                calendarTable.requestFocus();
                calendarTable.changeSelection(row, column, false, false);
            }
        }
        //center the text
        tableRenderer.setHorizontalAlignment(JLabel.CENTER);

        //set all columns to the table renderer
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            calendarTable.getColumnModel().getColumn(i).setCellRenderer(tableRenderer);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        calendarPanel = new javax.swing.JPanel();
        calendarScrollPane = new javax.swing.JScrollPane();
        calendarTable = new javax.swing.JTable();
        selectButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        controlPanel = new javax.swing.JPanel();
        prevYearButton = new javax.swing.JButton();
        prevMonthButton = new javax.swing.JButton();
        monthLabel = new javax.swing.JLabel();
        forwardMonthButton = new javax.swing.JButton();
        forwardYearButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setTitle("Date Selection");
        setResizable(false);

        calendarTable.setModel(tableModel);
        calendarTable.setCellSelectionEnabled(true);
        calendarScrollPane.setViewportView(calendarTable);

        javax.swing.GroupLayout calendarPanelLayout = new javax.swing.GroupLayout(calendarPanel);
        calendarPanel.setLayout(calendarPanelLayout);
        calendarPanelLayout.setHorizontalGroup(
            calendarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, calendarPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(calendarScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        calendarPanelLayout.setVerticalGroup(
            calendarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calendarPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(calendarScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        selectButton.setText("OK");
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        prevYearButton.setText("<<");
        prevYearButton.setToolTipText("Move back a year.");
        prevYearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevYearButtonActionPerformed(evt);
            }
        });

        prevMonthButton.setText("<");
        prevMonthButton.setToolTipText("Move back a month.");
        prevMonthButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevMonthButtonActionPerformed(evt);
            }
        });

        monthLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        monthLabel.setText("November 2014");
        monthLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        monthLabel.setPreferredSize(new java.awt.Dimension(100, 16));

        forwardMonthButton.setText(">");
        forwardMonthButton.setToolTipText("Move forward a month.");
        forwardMonthButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardMonthButtonActionPerformed(evt);
            }
        });

        forwardYearButton.setText(">>");
        forwardYearButton.setToolTipText("Move forward a year.");
        forwardYearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardYearButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addComponent(prevYearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(prevMonthButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(monthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(forwardMonthButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(forwardYearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(prevYearButton)
            .addComponent(prevMonthButton)
            .addComponent(monthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(forwardMonthButton)
            .addComponent(forwardYearButton)
        );

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Select a date to download resources from.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(147, 147, 147)
                        .addComponent(selectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(cancelButton))
                    .addComponent(controlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(calendarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(131, 131, 131))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(controlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calendarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        //sets the date to the initial date
        date.set(year, month, day);
        dateSelected = false;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Moves the calendar back a month. Year will roll back with it as well.
     *
     * @param evt
     */
    private void prevMonthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevMonthButtonActionPerformed
        if (selectedMonth == 0) {
            selectedMonth = 11; //moves back a year
            selectedYear -= 1;
        } else {
            selectedMonth -= 1; //back a month, same year
        }
        updateCalendar(selectedMonth, selectedYear);
    }//GEN-LAST:event_prevMonthButtonActionPerformed

    /**
     * Moves forward a month. Years will roll forward with it as well.
     *
     * @param evt
     */
    private void forwardMonthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardMonthButtonActionPerformed
        if (selectedMonth == 11) {
            selectedMonth = 0; //moves to a new year
            selectedYear += 1;
        } else {
            selectedMonth += 1; //back a month, same year
        }
        updateCalendar(selectedMonth, selectedYear);
    }//GEN-LAST:event_forwardMonthButtonActionPerformed

    /**
     * Moves back a year.
     *
     * @param evt
     */
    private void prevYearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevYearButtonActionPerformed
        selectedYear -= 1;
        updateCalendar(selectedMonth, selectedYear);
    }//GEN-LAST:event_prevYearButtonActionPerformed

    /**
     * Moves forward a year.
     *
     * @param evt
     */
    private void forwardYearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardYearButtonActionPerformed
        selectedYear += 1;
        updateCalendar(selectedMonth, selectedYear);
    }//GEN-LAST:event_forwardYearButtonActionPerformed

    /**
     * Grabs the selected date and disposes of the window.
     *
     * @param evt
     */
    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        //sets the date to the selected date
        date.set(selectedYear, selectedMonth, selectedDay);
        dateSelected = true;
        dispose();
    }//GEN-LAST:event_selectButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel calendarPanel;
    private javax.swing.JScrollPane calendarScrollPane;
    private javax.swing.JTable calendarTable;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton forwardMonthButton;
    private javax.swing.JButton forwardYearButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel monthLabel;
    private javax.swing.JButton prevMonthButton;
    private javax.swing.JButton prevYearButton;
    private javax.swing.JButton selectButton;
    // End of variables declaration//GEN-END:variables
}
