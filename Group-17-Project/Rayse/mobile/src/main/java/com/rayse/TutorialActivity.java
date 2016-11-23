package com.rayse;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TutorialActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        //Set typfaces for buttons on home screen
        Button signUp = (Button) findViewById(R.id.signUpButton);
        Button signIn = (Button) findViewById(R.id.signInButton);
        Typeface defualtTF = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        signUp.setTypeface(defualtTF);
        signIn.setTypeface(defualtTF);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //dynamically changing dots
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageView dot_1 = (ImageView) findViewById(R.id.dot1);
                ImageView dot_2 = (ImageView) findViewById(R.id.dot2);
                ImageView dot_3 = (ImageView) findViewById(R.id.dot3);
                if (position == 0) {
                    dot_1.setImageResource(R.drawable.selected_dot);
                    dot_2.setImageResource(R.drawable.empty_dot);
                    dot_3.setImageResource(R.drawable.empty_dot);
                } else if (position == 1) {
                    dot_1.setImageResource(R.drawable.empty_dot);
                    dot_2.setImageResource(R.drawable.selected_dot);
                    dot_3.setImageResource(R.drawable.empty_dot);
                } else {
                    dot_1.setImageResource(R.drawable.empty_dot);
                    dot_2.setImageResource(R.drawable.empty_dot);
                    dot_3.setImageResource(R.drawable.selected_dot);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int page = getArguments().getInt(ARG_SECTION_NUMBER);
            int layoutInt;
            if (page == 1) {
                layoutInt = R.layout.fragment_tutorial_1;
            } else if (page == 2) {
                layoutInt = R.layout.fragment_tutorial_2;
            } else {
                layoutInt = R.layout.fragment_tutorial_3;
            }
            View rootView = inflater.inflate(layoutInt, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    public void signup(View view) {
        DialogFragment dialog = new LoginDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("isSignup", true);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "LoginDialogFragment");
    }

    public void signin(View view) {
        if (UserModel.hasCurrentUser()) {
            enterApp();
        } else {
            DialogFragment dialog = new LoginDialogFragment();
            Bundle args = new Bundle();
            args.putBoolean("isSignup", false);
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "LoginDialogFragment");
        }
    }

    void enterApp() {
        Intent sendIntent = new Intent(this, NavigationActivity.class);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendIntent);
    }

    public static class LoginDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_signin, null);
            final EditText username = (EditText) dialogView.findViewById(R.id.signin_username);
            final EditText password = (EditText) dialogView.findViewById(R.id.signin_password);
            final EditText confirm = (EditText) dialogView.findViewById(R.id.signin_confirm);

            builder.setView(dialogView)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginDialogFragment.this.getDialog().cancel();
                        }
                    });
            if (getArguments().getBoolean("isSignup", false)) {
                builder.setPositiveButton(R.string.signup, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pw = password.getText().toString();
                        String confirmpw = confirm.getText().toString();
                        if (pw.equals(confirmpw)) {
                            UserModel.signup(getActivity(), username.getText().toString(), password.getText().toString());
                        } else {
                            Toast.makeText(getActivity(), R.string.signin_bad_confirm, Toast.LENGTH_SHORT);
                        }
                    }
                });
            } else {
                confirm.setVisibility(View.GONE);
                builder.setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserModel.signin(getActivity(), username.getText().toString(), password.getText().toString());
                    }
                });
            }
            return builder.create();
        }
    }
}