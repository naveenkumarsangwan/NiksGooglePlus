package com.niks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzazf;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.niks.googleplus.FriendActivity;

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<People.LoadPeopleResult> {
    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    SignInButton signIn_btn;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;
    ProgressDialog progress_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buidNewGoogleApiClient();
        setContentView(R.layout.activity_main);
        custimizeSignBtn();
        setBtnClickListeners();
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Signing in....");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void buidNewGoogleApiClient(){

        google_api_client =  new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }
    private void custimizeSignBtn(){

        signIn_btn = (SignInButton) findViewById(R.id.sign_in_button);
        signIn_btn.setSize(SignInButton.SIZE_STANDARD);
        signIn_btn.setScopes(new Scope[]{Plus.SCOPE_PLUS_LOGIN});

    }
    private void setBtnClickListeners(){
        // Button listeners
        signIn_btn.setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.frnd_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
    }

    protected void onStart() {
        super.onStart();
        google_api_client.connect();
    }

    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    protected void onResume(){
        super.onResume();
        if (google_api_client.isConnected()) {
            google_api_client.connect();
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            google_api_availability.getErrorDialog(this, result.getErrorCode(),request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

            connection_result = result;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        // Check which request we're responding to
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                progress_dialog.dismiss();

            }

            is_intent_inprogress = false;

            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        }

    }

    @Override
    public void onConnected(Bundle arg0) {
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout
        getProfileInfo();

        // Update the UI after signin
        changeUI(true);

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        google_api_client.connect();
        changeUI(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Toast.makeText(this, "start sign process", Toast.LENGTH_SHORT).show();
                gPlusSignIn();
                break;
            case R.id.sign_out_button:
                Toast.makeText(this, "Sign Out from G+", Toast.LENGTH_LONG).show();
                gPlusSignOut();

                break;
            case R.id.disconnect_button:
                Toast.makeText(this, "Revoke Access from G+", Toast.LENGTH_LONG).show();
                gPlusRevokeAccess();

            case R.id.frnd_button:
                Toast.makeText(this, "G+ Friend List", Toast.LENGTH_LONG).show();
                Plus.PeopleApi.loadVisible(google_api_client, null)
                        .setResultCallback(this);

                break;


        }
    }
    private void gPlusSignIn() {
        if (!google_api_client.isConnecting()) {
            Log.d("user connected", "connected");
            is_signInBtn_clicked = true;
            progress_dialog.show();
            resolveSignInError();

        }
    }
    private void gPlusRevokeAccess() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            Plus.AccountApi.revokeAccessAndDisconnect(google_api_client)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.d("MainActivity", "User access revoked!");
                            buidNewGoogleApiClient();
                            google_api_client.connect();
                            changeUI(false);
                            DrawerInfoRemove();
                        }

                    });
        }
    }
    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }
    }

    /*
      Sign-out from Google+ account
     */

    private void gPlusSignOut() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            google_api_client.disconnect();
            google_api_client.connect();
            changeUI(false);
            DrawerInfoRemove();
        }
    }

    private void DrawerInfoRemove() {

        TextView user_name1 = (TextView) findViewById(R.id.userName1);
        user_name1.setText(""+"");
        user_name1.clearFocus();
       /* TextView gemail_id1 = (TextView)findViewById(R.id.emailId1);
        gemail_id1.setText("Email: " +"");*/
        //gemail_id1.clearFocus();
        ImageView user_picture = (ImageView)findViewById(R.id.profile_pic1);
        user_picture.setImageResource(R.drawable.profilepic);
       // user_picture.setVisibility(View.INVISIBLE);

    }



    /*
     get user's information name, email, profile pic,Date of birth,tag line and about me
     */

    private void getProfileInfo() {

        try {

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
                setPersonalInfo(currentPerson);


            } else {
                Toast.makeText(getApplicationContext(),
                        "No Personal info mention", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void setDrawerProfilePic(String personPhotoUrl) {

        personPhotoUrl = personPhotoUrl.substring(0,
                personPhotoUrl.length() - 2)
                    + PROFILE_PIC_SIZE;
            ImageView user_picture = (ImageView)findViewById(R.id.profile_pic1);
            new LoadProfilePic(user_picture).execute(personPhotoUrl);

    }
    /*
     set the User information into the views defined in the layout
     */

    private void setPersonalInfo(Person currentPerson){

        String personName = currentPerson.getDisplayName();
        String personPhotoUrl = currentPerson.getImage().getUrl();
        String email = Plus.AccountApi.getAccountName(google_api_client);
        TextView user_name = (TextView) findViewById(R.id.userName);
        user_name.setText("Name: "+personName);
        TextView user_name1 = (TextView) findViewById(R.id.userName1);
        user_name1.setText("Name: "+personName);
        TextView gemail_id = (TextView)findViewById(R.id.emailId);
        gemail_id.setText("Email Id: " +email);
       /* TextView gemail_id1 = (TextView)findViewById(R.id.emailId1);
        gemail_id1.setText("Email: " +email);*/
        setProfilePic(personPhotoUrl);
        setDrawerProfilePic(personPhotoUrl);
        progress_dialog.dismiss();
        Toast.makeText(this, "Person information is shown!", Toast.LENGTH_LONG).show();
    }

    /*
     By default the profile pic url gives 50x50 px image.
     If you need a bigger image we have to change the query parameter value from 50 to the size you want
    */

    private void setProfilePic(String profile_pic){
        profile_pic = profile_pic.substring(0,
                profile_pic.length() - 2)
                + PROFILE_PIC_SIZE;
        ImageView user_picture = (ImageView)findViewById(R.id.profile_pic);
        new LoadProfilePic(user_picture).execute(profile_pic);
    }

    /*
     Show and hide of the Views according to the user login status
     */

    private void changeUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }


    @Override
    public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {
        if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
            ArrayList<String> list = new ArrayList<String>();
            ArrayList<String> img_list= new ArrayList<String>();
            try {
                int count = personBuffer.getCount();

                for (int i = 0; i < count; i++) {
                    list.add(personBuffer.get(i).getDisplayName());
                    img_list.add(personBuffer.get(i).getImage().getUrl());
                }
                Intent intent = new Intent(MainActivity.this,FriendActivity.class);
                intent.putStringArrayListExtra("friendsName",list);
                intent.putStringArrayListExtra("friendsPic",img_list);
                startActivity(intent);
            } finally {
                personBuffer.release();
            }
        } else {
            Log.e("circle error", "Error requesting visible circles: " + loadPeopleResult.getStatus());
        }
    }


   /*
    Perform background operation asynchronously, to load user profile picture with new dimensions from the modified url
    */

    private class LoadProfilePic extends AsyncTask<String, Void, Bitmap> {
        ImageView bitmap_img;

        public LoadProfilePic(ImageView bitmap_img) {
            this.bitmap_img = bitmap_img;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap new_icon = null;
            try {
                InputStream in_stream = new java.net.URL(url).openStream();
                new_icon = BitmapFactory.decodeStream(in_stream);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return new_icon;
        }

        protected void onPostExecute(Bitmap result_img) {

            bitmap_img.setImageBitmap(result_img);
        }
    }

}
