package koolpos.cn.goodsdisplayer.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.mvcModel.Goods;
import koolpos.cn.goodsdisplayer.ui.activity.ShowDetailActivity;
import koolpos.cn.goodsdisplayer.util.FileUtil;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayGoodGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayGoodGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayGoodGroupFragment extends BaseFragment {
    private Goods[] goods;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.gird_container)
    ViewGroup container;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param goods Goods to show.
     * @return A new instance of fragment DisplayGoodDetialFragment.
     */
    public static DisplayGoodGroupFragment newInstance(Goods[] goods) {
        DisplayGoodGroupFragment fragment = new DisplayGoodGroupFragment();
        Bundle args = new Bundle();
        args.putSerializable(Goods.class.getName(), goods);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            goods = (Goods[]) getArguments().getSerializable(Goods.class.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return super.onCreateView(inflater, (ViewGroup) inflater.inflate(R.layout.fragment_display_good_detial, container, false), savedInstanceState);
    }

    private void render(View itemView, final Goods itemGood) {
        TextView good_name = (TextView) itemView.findViewById(R.id.good_name);
        ImageView good_img= (ImageView) itemView.findViewById(R.id.good_img);
        try {
            Loger.d("render resumeRequests");
            Glide.with(getActivity())
                    .load(FileUtil.getImageCashFile(itemGood.getImage_url()))
    //               .load(itemGood.getImage_url())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.downloading)
                    .animate(R.anim.zoom_in)
                    .error(R.mipmap.download_error)
                    .into(good_img);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        good_name.setText(itemGood.getGoods_name());
        itemView.setClickable(true);
        itemView.setEnabled(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loger.d("onClick");
                Intent intent =new Intent(getContext(), ShowDetailActivity.class);
                intent.putExtra(Goods.class.getName(),itemGood);
                Bitmap cacheBmp = Bitmap.createBitmap(getActivity().getWindow().getDecorView().getWidth(), getActivity().getWindow().getDecorView().getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(cacheBmp);
                getActivity().getWindow().getDecorView().draw(canvas);
                MyApplication.CacheBitmap=cacheBmp;
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                cacheBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                byte[] bitmapByte = baos.toByteArray();
//                intent.putExtra(Bitmap.class.getName(), bitmapByte);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int count = container.getChildCount();
        Loger.d("count:"+count);
        for (int i = 0; i < container.getChildCount() && i<goods.length; i++) {
            View itemView = container.getChildAt(i);
            Goods itemGood = goods[i];
            if (itemGood != null) {
                render(itemView, itemGood);
            }
        }
//        TextView tv_hint_common = (TextView) view.findViewById(R.id.tv_hint_common);
//        if (goods!=null){
//            StringBuilder builder=new StringBuilder();
//            for (Goods item : goods){
//                builder.append(item.toString()+"\n");
//            }
//            tv_hint_common.setText(builder.toString());
//        }else{
//            tv_hint_common.setText("goods:"+null);
//        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
