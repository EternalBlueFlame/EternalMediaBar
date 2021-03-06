package com.ebf.eternalfinance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import com.ebf.eternalVariables.FinanceAccount;
import com.ebf.eternalVariables.Transaction;
import com.ebf.eternalmediabar.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EternalFinance extends Activity {

    public static EternalFinance activity;
    public static List<FinanceAccount> accounts = new ArrayList<FinanceAccount>();
    public static DisplayMetrics dpi = new DisplayMetrics();

    /**
     * <h2> initialize </h2>
     * setup the variables that won't normally change during use, then call the view builder
     */
    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_eternal_finance);
        activity = this;

        ((LinearLayout)findViewById(R.id.financepage)).addView(transactionList());

    }


    /**
     * <h2>build the list of transactions</h2>
     * this will generate lists of every transaction
     * @return
     */
    public static View transactionList(){
        //run GC then create the views.
        Runtime.getRuntime().gc();
        LinearLayout accountsLayout = new LinearLayout(EternalFinance.activity);


        for (final FinanceAccount account : accounts){
            /**
             * define the layouts for the accounts
             */
            LinearLayout layout = new LinearLayout(EternalFinance.activity);
            layout.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout entry = new RelativeLayout(EternalFinance.activity);
            TextView entryLabel = new TextView(EternalFinance.activity);
            entryLabel.setText(account.name);
            entryLabel.setMinimumWidth(Math.round(400 * EternalFinance.dpi.scaledDensity));
            entryLabel.setY((0 * EternalFinance.dpi.scaledDensity));
            entryLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            entry.addView(entryLabel);
            layout.addView(entry);

            //scroll view that contins a list view of entries
            ScrollView entryContainer = new ScrollView(EternalFinance.activity);
            LinearLayout entries = new LinearLayout(EternalFinance.activity);
            entries.setOrientation(LinearLayout.VERTICAL);

            /**
             * <h2>new transaction</h2>
             * create the entry for adding a new transaction.
             * The onClick will ask the user for all the appropriate data then add it to the save file before reloading everything.
             */
            TextView newEntry = new TextView(EternalFinance.activity);
            newEntry.setText("New Transaction");
            newEntry.setMinimumWidth(Math.round(400 * EternalFinance.dpi.scaledDensity));
            newEntry.setY((0 * EternalFinance.dpi.scaledDensity));
            newEntry.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            newEntry.setBackgroundColor(EternalFinance.activity.getResources().getColor(R.color.gray));
            newEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //use an alert dialogue to combine all the views for defining the new transation into a floating window.
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EternalFinance.activity);

                    LinearLayout layout = new LinearLayout(EternalFinance.activity);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    TextView newEntry = new TextView(EternalFinance.activity);
                    final EditText desc = new EditText(EternalFinance.activity);
                    desc.setHint("Transaction Description");
                    layout.addView(desc);
                    newEntry.setText("Transaction Amount: \n");
                    newEntry.setMinimumWidth(Math.round(400 * EternalFinance.dpi.scaledDensity));
                    newEntry.setY((TypedValue.COMPLEX_UNIT_SP * 20));
                    newEntry.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    layout.addView(newEntry);
                    final EditText et = new EditText(EternalFinance.activity);
                    et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    layout.addView(et);
                    TextView timeLabel = new TextView(EternalFinance.activity);
                    timeLabel.setText("Transaction Time: \n");
                    timeLabel.setMinimumWidth(Math.round(400 * EternalFinance.dpi.scaledDensity));
                    newEntry.setY((TypedValue.COMPLEX_UNIT_SP * 20));
                    timeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    layout.addView(timeLabel);
                    final EditText time = new EditText(EternalFinance.activity);
                    time.setInputType(InputType.TYPE_CLASS_DATETIME);
                    time.setText(android.text.format.DateFormat.getDateFormat(EternalFinance.activity).format(Calendar.getInstance().getTime()));
                    layout.addView(time);

                    alertDialogBuilder.setView(layout);

                    // add buttons for OK and cancel
                    alertDialogBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            DateFormat format = android.text.format.DateFormat.getDateFormat(EternalFinance.activity);
                            Date eventTime = Calendar.getInstance().getTime();
                            try {
                                eventTime = format.parse(time.getText().toString());
                            } catch (Exception e){}

                            Transaction event = new Transaction();
                            event.balance = account.currentBalance;
                            event.change = Float.parseFloat(et.getText().toString());
                            event.Description = desc.getText().toString();
                            event.time= eventTime;
                            account.transactions.add(event);
                            account.currentBalance = event.balance + event.change;
                            ((LinearLayout)EternalFinance.activity.findViewById(R.id.financepage)).removeAllViews();
                            ((LinearLayout)EternalFinance.activity.findViewById(R.id.financepage)).addView(transactionList());

                        }
                    });
                    //now show the dialogue.
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
            entries.addView(newEntry);

            //sort the list first
            Collections.sort(account.transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction lhs, Transaction rhs) {
                    return lhs.time.compareTo(rhs.time);
                }
            });

            /**
             * <h3> List the transactions </h3>
             * now we actually list the transactions and categorize them vertically by year and month.
             */
            int year =0;
            int month =0;
            boolean darkItem = false;
            for (Transaction event : account.transactions){
                //handle year, note date.getYar returns how many years it has been since 1900
                if (year ==0 & year != event.time.getYear()){
                    year = event.time.getYear();
                    TextView yearLabel = new TextView(EternalFinance.activity);
                    yearLabel.setText("Year: " + (1900+year));
                    yearLabel.setMinimumWidth(Math.round(400 * EternalFinance.dpi.scaledDensity));
                    yearLabel.setY((0 * EternalFinance.dpi.scaledDensity));
                    yearLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    yearLabel.setBackgroundColor(EternalFinance.activity.getResources().getColor(R.color.black));
                    yearLabel.setTextColor(EternalFinance.activity.getResources().getColor(R.color.white));
                    entries.addView(yearLabel);
                    //add an empty space
                    Space spacer = new Space(EternalFinance.activity);
                    spacer.setMinimumHeight(Math.round(EternalFinance.dpi.heightPixels *0.25F));
                    entries.addView(spacer);
                }

                //handle year
                if (month ==0 || month != event.time.getMonth()){
                    month = event.time.getMonth();
                    TextView monthLabel = new TextView(EternalFinance.activity);
                    monthLabel.setText("Month: "+month);
                    monthLabel.setMinimumWidth(Math.round(400 * EternalFinance.dpi.scaledDensity));
                    monthLabel.setY((0 * EternalFinance.dpi.scaledDensity));
                    monthLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    monthLabel.setBackgroundColor(EternalFinance.activity.getResources().getColor(R.color.black));
                    monthLabel.setTextColor(EternalFinance.activity.getResources().getColor(R.color.white));
                    entries.addView(monthLabel);
                    //add an empty space
                    Space spacer = new Space(EternalFinance.activity);
                    spacer.setMinimumHeight(Math.round(EternalFinance.dpi.heightPixels *0.25F));
                    entries.addView(spacer);
                }

                TextView eventLabel = new TextView(EternalFinance.activity);
                StringBuilder display = new StringBuilder();
                display.append(event.Description);
                display.append("\n");
                display.append(android.text.format.DateFormat.getDateFormat(EternalFinance.activity).format(event.time));
                display.append("\n");
                display.append("old: ");
                display.append(event.balance);
                display.append("\nchange: ");
                display.append(event.change);
                display.append("\nBalance: ");
                display.append(event.balance + event.change);
                eventLabel.setText(display.toString());
                eventLabel.setMinimumWidth(Math.round(EternalFinance.dpi.widthPixels * 0.5F));
                eventLabel.setY((0 * EternalFinance.dpi.scaledDensity));
                eventLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                if (darkItem) {
                    eventLabel.setBackgroundColor(EternalFinance.activity.getResources().getColor(R.color.darkGray));
                } else {
                    eventLabel.setBackgroundColor(EternalFinance.activity.getResources().getColor(R.color.gray));
                }
                darkItem = !darkItem;
                entries.addView(eventLabel);
                //add an empty space
                Space spacer = new Space(EternalFinance.activity);
                spacer.setMinimumHeight(Math.round(EternalFinance.dpi.heightPixels *0.25F));
                entries.addView(spacer);
            }

            entryContainer.addView(entries);
            layout.addView(entryContainer);

            accountsLayout.addView(layout);

        }

        /**
         * create an entry for adding a new account.
         */
        TextView newAccount = new TextView(EternalFinance.activity);
            newAccount.setText("Add an account");
            newAccount.setMinimumWidth(Math.round(400 * EternalFinance.dpi.scaledDensity));
            newAccount.setY((0 * EternalFinance.dpi.scaledDensity));
            newAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            newAccount.setBackgroundColor(EternalFinance.activity.getResources().getColor(R.color.gray));
            newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * create a dialogue builder to ask the user for the info necessary to add an account.
                 */
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EternalFinance.activity);
                final EditText et = new EditText(EternalFinance.activity);
                alertDialogBuilder.setView(et);
                alertDialogBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FinanceAccount tempAccount = new FinanceAccount();
                        tempAccount.name = et.getText().toString();
                        EternalFinance.accounts.add(tempAccount);
                        ((LinearLayout)EternalFinance.activity.findViewById(R.id.financepage)).removeAllViews();
                        ((LinearLayout)EternalFinance.activity.findViewById(R.id.financepage)).addView(transactionList());
                    }
                });

                // display the dialogue
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        accountsLayout.addView(newAccount);


        return accountsLayout;
    }


    /**
     * <h2> save the data on change</h2>
     * TODO: this part is still unfinished so the menu never saves.
     */
    public void saveData(){
        StringBuilder save = new StringBuilder();
        save.append("<financeSave>\n");
        for (FinanceAccount account : accounts){
            save.append("<account>\n<balance>");
            save.append(account.currentBalance);
            save.append("</balance>\n<name>");
            save.append(account.name);
            save.append("</name>\n");
            for (Transaction transaction : account.transactions){

            }
            save.append("</account>\n");
        }
        save.append("</financeSave>");
    }
    public void loadData(){

    }
}
