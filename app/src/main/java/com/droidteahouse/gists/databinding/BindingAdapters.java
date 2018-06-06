package com.droidteahouse.gists.databinding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidteahouse.gists.vo.Gist;
import com.squareup.picasso.Picasso;
import com.teahouse.gists.BR;
import com.teahouse.gists.R;

import java.util.List;

public class BindingAdapters {
  @BindingAdapter({"imageUrl"})
  public static void loadImage(ImageView view, String url) {
    Picasso.with(view.getContext()).load(url)
        .placeholder(R.mipmap.octocat)
        .error(R.mipmap.octocat)
        .fit().centerInside().into(view);
  }

  @BindingAdapter({"entries", "layout"})
  public static <T> void setEntries(ViewGroup viewGroup,
                                    List<Gist.AttachedFile> entries, int layoutId) {
    viewGroup.removeAllViews();
    if (entries != null) {
      LayoutInflater inflater = (LayoutInflater)
          viewGroup.getContext()
              .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      for (int i = 0; i < entries.size(); i++) {
        Gist.AttachedFile entry = entries.get(i);
        ViewDataBinding binding = DataBindingUtil
            .inflate(inflater, layoutId, viewGroup, true);
        binding.setVariable(BR.data, entry);
      }
    }
  }

  @BindingAdapter({"urlLink"})
  public static void urlLink(TextView view, String url) {
    view.setText(Html.fromHtml(url));
    view.setMovementMethod(LinkMovementMethod.getInstance());
  }
}
