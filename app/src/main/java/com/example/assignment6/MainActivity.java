package com.example.assignment6;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    Database myDB;
    TextView balance;
    EditText editMonth;
    EditText editDay;
    EditText editYear;
    EditText editPrice;
    EditText editItem;
    EditText filterMonthFrom;
    EditText filterDayFrom;
    EditText filterYearFrom;
    EditText filterMonthTo;
    EditText filterDayTo;
    EditText filterYearTo;
    EditText filterPriceFrom;
    EditText filterPriceTo;
    Button btnAdd;
    Button btnSub;
    Button btnFilter;
    Button btnClearFilter;
    TableLayout history;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new Database(this);

        balance = (TextView) findViewById(R.id.balance);
        editMonth = (EditText) findViewById(R.id.editMonth);
        editDay = (EditText) findViewById(R.id.editDay);
        editYear = (EditText) findViewById(R.id.editYear);
        editPrice = (EditText) findViewById(R.id.editPrice);
        editItem = (EditText) findViewById(R.id.editItem);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSub = (Button) findViewById(R.id.btnSub);
        btnFilter = (Button) findViewById(R.id.btnFilter);

        filterMonthFrom = (EditText) findViewById(R.id.filterMonthFrom);
        filterDayFrom = (EditText) findViewById(R.id.filterDayFrom);
        filterYearFrom = (EditText) findViewById(R.id.filterYearFrom);
        filterMonthTo = (EditText) findViewById(R.id.filterMonthTo);
        filterDayTo = (EditText) findViewById(R.id.filterDayTo);
        filterYearTo = (EditText) findViewById(R.id.filterYearTo);
        filterPriceFrom = (EditText) findViewById(R.id.filterPriceFrom);
        filterPriceTo = (EditText) findViewById(R.id.filterPriceTo);
        history = (TableLayout) findViewById(R.id.tableHistory);
        GetAllHistory();
        AddTransaction();
        Filter();

    }

    public void AddTransaction(){
        btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = Double.parseDouble(editPrice.getText().toString());
                        Model model = new Model();
                        String month = editMonth.getText().toString();
                        String day = editDay.getText().toString();
                        String year = editYear.getText().toString();
                        model.mDate =  CreateDate(month, day, year);
                        model.mItem = editItem.getText().toString();
                        model.mPrice = price;
                        boolean result = myDB.createTransaction(model);
                        if (result)
                            Toast.makeText(MainActivity.this, "Transaction Created", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Transaction Not Created", Toast.LENGTH_LONG).show();
                        GetAllHistory();
                        ClearText();
                        closeKeyboard();
                    }
                }
        );

        btnSub.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = -1 * Double.parseDouble(editPrice.getText().toString());
                        Model model = new Model();
                        String month = editMonth.getText().toString();
                        String day = editDay.getText().toString();
                        String year = editYear.getText().toString();
                        model.mDate =  CreateDate(month, day, year);
                        model.mItem = editItem.getText().toString();
                        model.mPrice = price;
                        boolean result = myDB.createTransaction(model);
                        if (result)
                            Toast.makeText(MainActivity.this, "Transaction Created", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Transaction Not Created", Toast.LENGTH_LONG).show();
                        GetAllHistory();
                        ClearText();
                        closeKeyboard();
                    }
                }
        );
    }

    public void GetAllHistory(){
        Cursor result = myDB.getAllData();
        DisplayHistory(result, false);
    }

    public void DisplayHistory(Cursor result, boolean filtered){
        if (result == null){
            return;
        }

        StringBuffer buffer = new StringBuffer();
        Double balance = 0.0;
        ClearTable();
        while(result.moveToNext()){
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams columnLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            columnLayout.weight = 1;

            TextView date = new TextView(this);
            date.setLayoutParams(columnLayout);
            date.setText(result.getString(2));
            tr.addView(date);

            TextView priceView = new TextView(this);
            priceView.setLayoutParams(columnLayout);
            priceView.setText(result.getString(3));
            tr.addView(priceView);

            TextView item = new TextView(this);
            item.setLayoutParams(columnLayout);
            item.setText(result.getString(1));
            tr.addView(item);

            history.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            // get price for balance
            double price = Double.parseDouble(result.getString(3));
            balance += price;
        }
        if (!filtered){
            MainActivity.this.balance.setText("Current Balance: $" + df.format(balance));
        }
    }

    public void ClearText(){
        MainActivity.this.editMonth.setText("");
        MainActivity.this.editDay.setText("");
        MainActivity.this.editYear.setText("");
        MainActivity.this.editPrice.setText("");
        MainActivity.this.editItem.setText("");
        MainActivity.this.filterMonthFrom.setText("");
        MainActivity.this.filterDayFrom.setText("");
        MainActivity.this.filterYearFrom.setText("");
        MainActivity.this.filterMonthTo.setText("");
        MainActivity.this.filterDayTo.setText("");
        MainActivity.this.filterYearTo.setText("");
        MainActivity.this.filterPriceFrom.setText("");
        MainActivity.this.filterPriceTo.setText("");
    }

    public void ClearTable(){
        int count = history.getChildCount();
        for (int i = 1; i < count; i++) {
            history.removeViewAt(1);
        }
    }

    public void Filter(){
        btnFilter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String priceFromString = filterPriceFrom.getText().toString();
                        String priceToString = filterPriceTo.getText().toString();
                        String month = filterMonthFrom.getText().toString();
                        String day = filterDayFrom.getText().toString();
                        String year = filterYearFrom.getText().toString();
                        String dateFrom = CreateDate(month, day, year);
                        month = filterMonthTo.getText().toString();
                        day = filterDayTo.getText().toString();
                        year = filterYearTo.getText().toString();
                        String dateTo = CreateDate(month, day, year);
                        closeKeyboard();

                        Cursor result = myDB.getFilteredData(priceFromString, priceToString, dateFrom, dateTo);
                        DisplayHistory(result, true);
                    }
                }
        );
    }

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public String CreateDate(String month, String day, String year){
        if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
            return "";
        }
        else {
            int monthIn = Integer.parseInt(month);
            int dayIn = Integer.parseInt(day);
            if (monthIn < 10 && dayIn >= 10) {
                return year + "-" + day + "-0" + month;
            }
            else if (monthIn >= 10 && dayIn < 10){
                return year + "-0" + day + "-" + month;
            }
            else if (monthIn < 10 && dayIn < 10){
                return year + "-0" + day + "-0" + month;
            }
            else {
                return year + "-" + day + "-" + month;
            }
        }
    }
}
