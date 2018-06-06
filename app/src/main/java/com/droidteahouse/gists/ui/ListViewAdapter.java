package com.droidteahouse.gists.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidteahouse.gists.viewmodel.ItemViewModel;
import com.droidteahouse.gists.vo.Gist;
import com.squareup.picasso.Picasso;
import com.teahouse.gists.R;
import com.teahouse.gists.databinding.ItemBinding;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder> {
  private List<Gist> mGists;

  public ListViewAdapter(List<Gist> items) {
    mGists = items;
    setHasStableIds(true);
  }

  @Override
  public ListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View gistView = inflater.inflate(R.layout.item, parent, false);
    ViewHolder viewHolder = new ViewHolder(gistView);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ListViewAdapter.ViewHolder viewHolder, int position) {
    Gist gist = (Gist) mGists.get(position);
    viewHolder.binding.setItemViewModel(new ItemViewModel(gist));
    //viewHolder.binding.itemImage.setVisibility(showImage.get() ? View.VISIBLE : View.GONE);
    viewHolder.binding.executePendingBindings();
  }

  @Override
  public int getItemCount() {
    return mGists == null ? 0 : mGists.size();
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    return position;
  }

  @Override
  public void onViewRecycled(ViewHolder holder) {
    super.onViewRecycled(holder);
    Picasso.with(holder.itemView.getContext())
        .cancelRequest(holder.binding.itemImage);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    final ItemBinding binding;

    public ViewHolder(View itemView) {
      super(itemView);
      binding = ItemBinding.bind(itemView);
    }
  }
}
