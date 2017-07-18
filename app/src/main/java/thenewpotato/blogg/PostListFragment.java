package thenewpotato.blogg;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import thenewpotato.blogg.managers.DateSetter;
import thenewpotato.blogg.managers.PostsAdapter;
import thenewpotato.blogg.managers.TimeSetter;
import thenewpotato.blogg.objects.Post;

import static thenewpotato.blogg.Tools.OPTION_A_TO_Z;
import static thenewpotato.blogg.Tools.OPTION_NEWEST;
import static thenewpotato.blogg.Tools.OPTION_OLDEST;
import static thenewpotato.blogg.Tools.OPTION_Z_TO_A;
import static thenewpotato.blogg.Tools.log;
import static thenewpotato.blogg.Tools.loge;
import static thenewpotato.blogg.objects.Post.STATUS_LIVE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PostListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostListFragment extends Fragment{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //"list_type" = "published" when displaying published posts, "list_type" = "draft" when displaying drafts
    private static final String ARG_PARAM1 = "list_type";
    private static final String ARG_PARAM2 = "posts";
    private static final String ARG_PARAM3 = "sort";

    private String mStatus;
    private ArrayList<Post> mPosts;
    private int mSortOption;
    ListView lvMain;

    public PostListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PostListFragment.
     */
    public static PostListFragment newInstance(String param1, ArrayList<Post> param2, int param3) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putParcelableArrayList(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStatus = getArguments().getString(ARG_PARAM1);
            mPosts = getArguments().getParcelableArrayList(ARG_PARAM2);
            mSortOption = getArguments().getInt(ARG_PARAM3);
        }
        log("PostListFragment created under parameters [PARAM1=" + mStatus + ", PARAM2(MAP)Present?=" +
                !(mPosts==null) + "]!" + " " + String.valueOf(mSortOption));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        //ADD CODE AFTER THIS LINE

        ImageView ivSort = (ImageView) getActivity().findViewById(R.id.iv_sort);
        ivSort.setVisibility(View.VISIBLE);

        lvMain = (ListView) view.findViewById(R.id.listview_posts);

        sort(mSortOption);

        final PostsAdapter adapter = new PostsAdapter(getContext(), mPosts);
        lvMain.setAdapter(adapter);
        lvMain.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvMain.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                nr = 0;
                MenuInflater inflater = getActivity().getMenuInflater();
                if(mStatus.equals(STATUS_LIVE)) {
                    inflater.inflate(R.menu.contextual_menu_live, menu);
                } else {
                    inflater.inflate(R.menu.contextual_menu_draft, menu);
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.item_delete:
                        nr = 0;
                        new AlertDialog.Builder(getActivity())
                                .setIcon(R.drawable.ic_delete_black_24dp)
                                .setTitle("Deleting Posts")
                                .setMessage("Are you sure you want delete selected posts?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        adapter.delete(mPosts);
                                        actionMode.finish();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                        return true;
                    case R.id.item_post:
                        nr = 0;

                        View dialogView = View.inflate(getActivity(), R.layout.dialog_publish, null);
                        final TextView tvDatePicker = (TextView) dialogView.findViewById(R.id.tv_date_picker);
                        final TextView tvTimePicker = (TextView) dialogView.findViewById(R.id.tv_time_picker);
                        final RadioButton rbPublishNow = (RadioButton) dialogView.findViewById(R.id.rb_publish_now);
                        final RadioButton rbSchedulePost = (RadioButton) dialogView.findViewById(R.id.rb_schedule_post);

                        rbPublishNow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if(b){
                                    rbSchedulePost.setChecked(false);
                                }
                            }
                        });

                        rbSchedulePost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if(b){
                                    rbPublishNow.setChecked(false);
                                }
                            }
                        });

                        new DateSetter(tvDatePicker, getActivity());
                        new TimeSetter(tvTimePicker, getActivity());

                        new AlertDialog.Builder(getActivity())
                                .setIcon(R.drawable.ic_cloud_done_black_24dp)
                                .setTitle("Publishing Posts")
                                .setView(dialogView)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // if the user did not specify a time, publish immediately
                                        if(rbPublishNow.isChecked()) {
                                            adapter.publish(mPosts, null);
                                        } else{
                                            log("went through scheudled formatting");
                                            SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm", Locale.US);
                                            try{
                                                Date convertedDate = format.parse(tvDatePicker.getText() + " " + tvTimePicker.getText());
                                                adapter.publish(mPosts, convertedDate);
                                            } catch (ParseException e){
                                                loge(e.getMessage());
                                            }
                                        }
                                        actionMode.finish();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                        return true;
                        //NOT SURE ON RETURN STATEMENT
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                adapter.clearSelection();
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                if (checked) {
                    nr++;
                    adapter.setNewSelection(position);
                } else {
                    nr--;
                    adapter.removeSelection(position);
                }
                mode.setTitle(nr + " selected");
            }

        });

        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                lvMain.setItemChecked(i, !adapter.isPositionChecked(i));
                return false;
            }
        });

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                log("Selected post title: " + mPosts.get(i).title);
                ((MainActivity)getActivity()).updatePostCall(mPosts.get(i));
            }
        });

        //ADD CODE BEFORE THIS LINE

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPosts.clear();
    }

    /**
     * Sort the Post List with this function
     *
     * sortMode must be one of the following
     * "update_time" = 0
     * "reverse_update_time" = 1
     * "alphabetical" = 2
     * "reverse_alphabetical" = 3*/
    private void sort(int sortMode){
        switch (sortMode){
            case OPTION_OLDEST:
                Collections.sort(mPosts, Post.getDateComparator(false));
                break;
            case OPTION_NEWEST:
                Collections.sort(mPosts, Post.getDateComparator(true));
                //sortedPostIds = new ArrayList<>(mapTitles.keySet());
                break;
            case OPTION_A_TO_Z:
                Collections.sort(mPosts, Post.getTitleComparator(false));
                //mapTitles = Tools.sortByValue(mapTitles, false);
                //sortedPostIds = new ArrayList<>(mapTitles.keySet());
                break;
            case OPTION_Z_TO_A:
                Collections.sort(mPosts, Post.getTitleComparator(true));
                //mapTitles = Tools.sortByValue(mapTitles, true);
                //sortedPostIds = new ArrayList<>(mapTitles.keySet());
                break;
        }
    }

}
