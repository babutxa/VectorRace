package aferrer.vectorRace;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "aferrer.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void startGame(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    public void startSkeleton(View view){
        // Do something in response to button
        Intent intent = new Intent(this, SkeletonActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_help:
                openHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void  openSearch(){
        Toast.makeText(this, "Search button pressed", Toast.LENGTH_SHORT).show();
    }

    private void  openSettings(){
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

    private void openHelp(){
        Toast.makeText(this, "Help button pressed", Toast.LENGTH_SHORT).show();
    }
 }
