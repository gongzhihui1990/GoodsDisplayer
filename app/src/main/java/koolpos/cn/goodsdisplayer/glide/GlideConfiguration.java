package koolpos.cn.goodsdisplayer.glide;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/6/9.
 */

public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 配置图片将缓存到SD卡
        MyExternalCacheDiskCacheFactory externalCacheDiskCacheFactory = new MyExternalCacheDiskCacheFactory(context);
        builder.setDiskCache(externalCacheDiskCacheFactory);

        // 如果配置图片将缓存到SD卡后那么getPhotoCacheDir返回仍然没有变化
        Log.w("getPhotoCacheDir", Glide.getPhotoCacheDir(context).getPath());
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // 配置使用OKHttp来请求网络
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
    class MyExternalCacheDiskCacheFactory extends DiskLruCacheFactory {


        public MyExternalCacheDiskCacheFactory(Context context) {
            this(context,"image_disk_cache", DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE);
        }

        public MyExternalCacheDiskCacheFactory(Context context, int diskCacheSize) {
            this(context,"image_disk_cache", diskCacheSize);
        }

        public MyExternalCacheDiskCacheFactory(final Context context, final String diskCacheName, int diskCacheSize) {
            super(new CacheDirectoryGetter() {
                @Override
                public File getCacheDirectory() {
//                    File cacheDirectory = context.getExternalCacheDir();
                    File cacheDirectory = Environment.getExternalStorageDirectory();
                    if (cacheDirectory == null) {
                        return null;
                    }
                    if (diskCacheName != null) {
                        Log.w("getPhotoCacheDir-", cacheDirectory.getPath());
                        return new File(cacheDirectory, diskCacheName);
                    }
                    return cacheDirectory;
                }
            }, diskCacheSize);
        }
    }
}