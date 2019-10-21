package com.mei.tododemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mei.tododemo.R;
import com.mei.tododemo.model.TodoBean;
import com.mei.tododemo.tools.VerticalView;

import java.util.ArrayList;
import java.util.List;

/**
 * created by meishenbo
 * 2018/12/12
 */
public class MainRAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG ="MainRAdapter";
    private Context context;
    private ListItemClickListener listItemClickListener;
    private List<Object> datas;
    private RecyclerView mRecyclerView;
    public final static int LOAD_MORE=1;//加载
    public final static int NOT_LOAD_MORE=2;//没有更多了
    public final static int LOAD_NORMAL=0;//正常
    public final static int LOAD_HIDE=3;//不显示
    public final static int NORMAL=0;
    public final static int FOOT=1;
    public int load_status=0;


    public MainRAdapter(Context context) {
        this.context = context;
        datas = new ArrayList<>();
    }

    public void setListItemClickListener(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public void adddatas(List<Object> dataList) {
        if (dataList!=null) {
            datas.clear();
            datas.addAll(dataList);
            notifyDataSetChanged();
        }
    }



    public void setLoad_status(int load_status) {
        this.load_status = load_status;
        Log.d(TAG, "setLoad_status: "+load_status);
        notifyDataSetChanged();
    }

    public int getLoad_status() {
        return load_status;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i==FOOT){
            View inflate2 = LayoutInflater.from(context).inflate(R.layout.foot_layout, viewGroup, false);
            FootViewHolder footViewHolder = new FootViewHolder(inflate2);
            return footViewHolder;
        }else {
            View inflate = LayoutInflater.from(context).inflate(R.layout.list_vertical_item, viewGroup, false);
            MViewHolder mViewHolder = new MViewHolder(inflate);
            return mViewHolder;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof  MViewHolder){
            MViewHolder mViewHolder = (MViewHolder) viewHolder;
            TodoBean todoBean = (TodoBean) datas.get(i);
            if (todoBean.getTitle()!=null) {
                mViewHolder.title.setText(todoBean.getTitle());
            }

            if (todoBean.getCtime()!=null){
                mViewHolder.time.setText(todoBean.getCtime());
            }
        }else if(viewHolder instanceof FootViewHolder){
            FootViewHolder footViewHolder = (FootViewHolder) viewHolder;
            switch (load_status){
                case LOAD_MORE:
                    footViewHolder.itemView.setVisibility(View.VISIBLE);
                    footViewHolder.foot_view.setVisibility(View.GONE);
                    footViewHolder.load_more.setVisibility(View.VISIBLE);

                    break;
                case LOAD_NORMAL:
                    footViewHolder.itemView.setVisibility(View.VISIBLE);
                    footViewHolder.foot_view.setText("上拉加载更多!");
                    footViewHolder.foot_view.setVisibility(View.VISIBLE);
                    footViewHolder.load_more.setVisibility(View.GONE);
                    break;
                case NOT_LOAD_MORE:
                    footViewHolder.itemView.setVisibility(View.VISIBLE);
                    footViewHolder.foot_view.setText("没有更多数据了!");
                    footViewHolder.foot_view.setVisibility(View.VISIBLE);
                    footViewHolder.load_more.setVisibility(View.GONE);
                    break;

                case LOAD_HIDE:
                    footViewHolder.itemView.setVisibility(View.GONE);
                    break;
            }
        }






    }

    @Override
    public int getItemCount() {
        return datas.size()!=0?datas.size()+1:0;
    }

    @Override
    public int getItemViewType(int position) {
            if (datas.size()==position) {
                return FOOT;
            }else {
                return NORMAL;
            }

    }

    class FootViewHolder extends RecyclerView.ViewHolder{
            TextView foot_view;
            LinearLayout load_more;


        public FootViewHolder(@NonNull View itemView) {
            super(itemView);

            foot_view = itemView.findViewById(R.id.foot_view);
            load_more = itemView.findViewById(R.id.load_more);
        }
    }


    class MViewHolder extends RecyclerView.ViewHolder{
        VerticalView verticalView;
        TextView title;
        TextView time;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            verticalView= itemView.findViewById(R.id.vertical_viw);
            title = itemView.findViewById(R.id.vertical_title);
            time = itemView.findViewById(R.id.vertical_time);
            if (listItemClickListener!=null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listItemClickListener.onItemClick(mRecyclerView,getAdapterPosition(),view);
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        listItemClickListener.onItemLongClick(mRecyclerView,getAdapterPosition(),view);
                        return true;
                    }
                });
            }


        }
    }

    public interface ListItemClickListener {
        void onItemClick(RecyclerView recyclerView, int position, View view);
        void onItemLongClick(RecyclerView recyclerView, int position, View view);
    }
}
