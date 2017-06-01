package koolpos.cn.goodsdisplayer.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.PopupWindow;
import android.widget.Scroller;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.api.SkuTypeManger;
import koolpos.cn.goodsdisplayer.mvcModel.GoodType;
import koolpos.cn.goodsdisplayer.mvcModel.Goods;
import koolpos.cn.goodsdisplayer.ui.adapter.DisplaySkuAdapter;
import koolpos.cn.goodsdisplayer.ui.fragment.DisplayGoodGroupFragment;
import koolpos.cn.goodsdisplayer.ui.widget.BounceBackViewPager;
import koolpos.cn.goodsdisplayer.ui.widget.DividerGridItemDecoration;
import koolpos.cn.goodsdisplayer.ui.widget.FixedSpeedScroller;
import koolpos.cn.goodsdisplayer.ui.widget.GridSpacingItemDecoration;
import koolpos.cn.goodsdisplayer.ui.widget.TypePopupWindow;
import koolpos.cn.goodsdisplayer.util.AndroidUtils;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DisplayActivity extends BaseActivity implements DisplayGoodGroupFragment.OnFragmentInteractionListener {

    @BindView(R.id.grid_content)
    RecyclerView gridContentView;
    @BindView(R.id.select_type)
    TextView tvSelectType;
    private AidlApi aidlApi;
    /**
     * 当前分类标记位
     */
    private int selectedIndex = -1;
    /**
     * 分类列表
     */
    private List<GoodType> types;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IGPService gpService = IGPService.Stub.asInterface(iBinder);
            aidlApi = new AidlApi(gpService);
            final DisplaySkuAdapter gridAdapter=new DisplaySkuAdapter(aidlApi);
            GridSpacingItemDecoration dividerGridItemDecoration =new GridSpacingItemDecoration(Integer.MAX_VALUE,20,true);
            gridContentView.addItemDecoration(dividerGridItemDecoration);
            gridContentView.setAdapter(gridAdapter);
            try {
                types = aidlApi.getTypeList();
                if (selectedIndex == -1) {
                    selectedIndex = 0;
                    gridAdapter.setType(types.get(0).getTypeName());
                }
                tvSelectType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TypePopupWindow.OnSPUSelectedListener spuSelectedListener = new TypePopupWindow.OnSPUSelectedListener() {
                            @Override
                            public void onSelected(/*GoodType type,*/int index) {
                                selectedIndex = index;
                                GoodType type = types.get(selectedIndex);
                                gridAdapter.setType(type.getTypeName());
                            }
                        };
                        showPopFormBottom(types, selectedIndex, spuSelectedListener);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private WindowManager.LayoutParams params;

    private void showPopFormBottom(List<GoodType> data, int selectedIndex, TypePopupWindow.OnSPUSelectedListener spuSelectedListener) {
        TypePopupWindow popupWindow = new TypePopupWindow(getBaseContext(), selectedIndex, data, spuSelectedListener);
        popupWindow.showAtLocation(findViewById(R.id.main_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        params = getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha = 0.7f;
        getWindow().setAttributes(params);
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Glide.with(MyApplication.getContext()).resumeRequests();
             //Title布局
//        GridLayoutManager titleLayoutManager = new GridLayoutManager(this,1);
//        titleLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
//        listContentTitle.setLayoutManager(titleLayoutManager);
//        listContentTitle.setAdapter(typeAdapter);
        gridContentView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL));
        Intent serviceIntent = new Intent(IGPService.class.getName());
        serviceIntent = AndroidUtils.getExplicitIntent(getBaseContext(), serviceIntent);
        boolean bindService = bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        start();
        gridContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //监听ViewPager的触摸事件，当用户按下的时候取消注册，当用户手抬起的时候再注册
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        stop();
                        break;
                    case MotionEvent.ACTION_UP:
                        stop();
                        reStart();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        stop();
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private int mCurrentPage = 0;
    private boolean isAutoPlay = true;
    private Disposable mViewPagerSubscribe;
    private static final Interpolator sInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    private boolean stateStop = true;


    //开始轮播
    public void start() {
    }

    //重新轮播
    public void reStart() {
    }

    //结束、暂停轮播
    public void stop() {
    }



    public class GoodTypeAdapter extends RecyclerView.Adapter<GoodTypeAdapter.GoodTypeViewHolder> {

        private List<GoodType> data = new ArrayList<>();
        private int curIndex = 0;
        private SkuTypeManger skuTypeManger;

        private GoodTypeAdapter(SkuTypeManger skuTypeManger) {
            this.skuTypeManger = skuTypeManger;
        }

        public void setData(List<GoodType> data) {
            this.data = data;
            myNotifyDataSetChanged();
        }

        public void myNotifyDataSetChanged() {
            skuTypeManger.setType(data.get(curIndex).getTypeName());
            notifyDataSetChanged();
        }

        @Override
        public GoodTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_good_title, parent, false);
            return new GoodTypeViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final GoodTypeViewHolder holder, int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (curIndex != holder.getAdapterPosition()) {
                        curIndex = holder.getAdapterPosition();
                        myNotifyDataSetChanged();
                    }
                }
            });
            holder.good_type.setSelected(curIndex == position);
            holder.good_type.setText(data.get(position).getTypeName());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class GoodTypeViewHolder extends RecyclerView.ViewHolder {
            TextView good_type;

            private GoodTypeViewHolder(View itemView) {
                super(itemView);
                good_type = (TextView) itemView.findViewById(R.id.good_type);
            }
        }
    }
}
