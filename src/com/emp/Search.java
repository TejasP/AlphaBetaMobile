package com.emp;

import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.emp.library.DatabaseHandler;
import com.emp.library.UserFunctions1;
 
public class Search extends Activity {
    Button btnSearch;
    EditText inputSearch;    
    TextView searchErrorMsg;
 
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
 
        // Importing all assets like buttons, text fields
        inputSearch = (EditText) findViewById(R.id.inputSearch);        
        btnSearch = (Button) findViewById(R.id.btnSearch);        
        searchErrorMsg = (TextView) findViewById(R.id.search_error);
 
        // Search button Click Event
        btnSearch.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                String search_string = inputSearch.getText().toString();
                UserFunctions1 userFunction = new UserFunctions1();
                JSONObject json = userFunction.search(search_string);
 
                // check for search response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        searchErrorMsg.setText("");
                        String res = json.getString(KEY_SUCCESS);
                        if(Integer.parseInt(res) == 1){
                            // user successfully logged in
                            // Store user details in SQLite Database
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            JSONObject json_user = json.getJSONObject("user");
                             
                            // Clear all previous data in database
                            userFunction.logoutUser(getApplicationContext());
                            db.addUser1(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));                       
                             
                            // Launch Dashboard Screen
                            Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                             
                            // Close all views before launching Dashboard
                            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(dashboard);
                             
                            // Close Login Screen
                            finish();
                        }else{
                            // Error in login
                            searchErrorMsg.setText("No matching product found");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
