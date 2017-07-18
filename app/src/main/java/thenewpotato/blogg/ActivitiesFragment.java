package thenewpotato.blogg;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import thenewpotato.blogg.managers.CommentsAdapter;
import thenewpotato.blogg.managers.GetDetailedCommentsTask;
import thenewpotato.blogg.objects.Comment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivitiesFragment extends Fragment {

    private static final String ARG_PARAM1 = "t_count";
    private static final String ARG_PARAM2 = "primitive_comments";
    private static final int INDEX_7 = 0;
    private static final int INDEX_30 = 1;
    private static final int INDEX_ALL = 2;

    private ArrayList<String> mTrafficCount;
    private ArrayList<Comment> mPrimitiveComments;
    // flag_loading is explicitly for onScrollListener, so that the listener does not invoke itself multiple
    // times while the previous invocation is in process
    private boolean flag_loading = false;
    // flag_no_more_comments tracks whether or not there is any more comments in mPrimitiveComments to sort
    private boolean flag_no_more_comments = false;
    // startingCount is explicitly for the sake of simplicity when filtering comments
    // it tracks where in the mPrimitiveComments the last filter has left off
    // so the next filter process can simply pick up where left off
    private int startingCount = 0;

    ListView lvMain;
    CommentsAdapter adapter;
    TextView tv7Days;
    TextView tv30Days;
    TextView tvAllTime;


    public ActivitiesFragment() {
        // Required empty public constructor
    }

    public static ActivitiesFragment newInstance(ArrayList<String> param1, ArrayList<Comment> param2) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, param1);
        args.putParcelableArrayList(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrafficCount = getArguments().getStringArrayList(ARG_PARAM1);
            mPrimitiveComments = getArguments().getParcelableArrayList(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities, container, false);

        lvMain = (ListView) view.findViewById(R.id.lv_comment);
        tv7Days = (TextView) view.findViewById(R.id.tv_7day_view);
        tv30Days = (TextView) view.findViewById(R.id.tv_30ay_view);
        tvAllTime = (TextView) view.findViewById(R.id.tv_all_days_view);

        tv7Days.setText(mTrafficCount.get(INDEX_7));
        tv30Days.setText(mTrafficCount.get(INDEX_30));
        tvAllTime.setText(mTrafficCount.get(INDEX_ALL));

        ImageView ivSort = (ImageView) getActivity().findViewById(R.id.iv_sort);
        ivSort.setVisibility(View.GONE);

        // adapter is initialized in onPostExecute
        final GetDetailedCommentsTask task = new GetDetailedCommentsTask(MainActivity.mAuthorizedAccount, getActivity()) {
            @Override
            protected void onPostExecute(ArrayList<Comment> result){
                super.onPostExecute(result);
                adapter = new CommentsAdapter(getContext(), result, false);
                lvMain.setAdapter(adapter);
            }
        };
        task.execute(getTenFilteredComments());

        // onScrollListener invoked when reaching the bottom of the ListView
        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                // nothing here
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    // if there is no more comments to sort, there is no reason to keep invoking the filtering process
                    if(!flag_loading && !flag_no_more_comments)
                    {
                        flag_loading = true;
                        GetDetailedCommentsTask taskMore =
                                new GetDetailedCommentsTask(MainActivity.mAuthorizedAccount, getActivity()){
                                    @Override
                                    protected void onPostExecute(ArrayList<Comment> result){
                                        super.onPostExecute(result);
                                        adapter.addAll(result);
                                        adapter.notifyDataSetChanged();
                                        flag_loading = false;
                                    }
                                };
                        taskMore.execute(getTenFilteredComments());
                    }
                }
            }
        });
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Comment item = (Comment) adapterView.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putParcelableArrayListExtra(Tools.KEY_PRIMITIVE_COMMENTS, mPrimitiveComments);
                intent.putExtra(Tools.KEY_ROOT_COMMENT, item);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
/*
    private ArrayList<Comment> getFilteredComments(ArrayList<Comment> comments){
        ArrayList<Comment> result = new ArrayList<>();
        for(Comment comment : comments){
            try{
                if(comment.inReplyToId == null){
                    result.add(comment);
                }
            }catch (NullPointerException e){
                //
            }
        }
        return result;
    }
*/
    private ArrayList<Comment> getTenFilteredComments(){
        ArrayList<Comment> result = new ArrayList<>();
        for(int i = 0; i < 10; startingCount++){
            if(startingCount == mPrimitiveComments.size()){
                flag_no_more_comments = true;
                break;
            }
            if(mPrimitiveComments.get(startingCount).inReplyToId == null){
                result.add(mPrimitiveComments.get(startingCount));
                i++;
                Tools.log(String.valueOf(i) + " " + String.valueOf(startingCount));
            }
        }
        return result;
    }
/*
    // portal for MainActivity
    // method ONLY to be called by MainActivity
    public void addComments(ArrayList<Comment> mComments, String nextPageToken){
        mToken = nextPageToken;
        if(mToken != null) {
            adapter.addAll(mComments);
            adapter.notifyDataSetChanged();
            flag_loading = false;
        }
    }
*/
}
