package cn.crcrc.arkui_example.utils;

import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.app.AbilityContext;
import ohos.data.rdb.ValuesBucket;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImagePacker;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;

import java.io.*;

public class Utils {

    private AbilityContext abilityContext;

    // 定义日志标签
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0xF0303, "APP LOG");


    public Utils() {
    }

    public Utils(AbilityContext abilityContext) {
        this.abilityContext = abilityContext;
    }

    //存储照片到js可以访问的目录下
    public Boolean setImaData(Uri uri, String name) {
        String imgName = "/" + name + ".jpg";
        try {
            //该目录和JSUI中的internal://app/目录是一个目录
            File file = new File(abilityContext.getFilesDir() + imgName);
            HiLog.info(LABEL,"文件路径:" + file);
            System.out.println(" APP LOG 文件路径:" + file);
            if (file.exists()) {
                HiLog.info(LABEL,"文件已存在");
                System.out.println(" APP LOG 文件已存在");
                return false;
            }
            HiLog.info(LABEL,"文件不存在");
            System.out.println(" APP LOG 文件不存在");

            //定义数据能力帮助对象
            DataAbilityHelper helper = DataAbilityHelper.creator(abilityContext);
            //读取图片
            FileDescriptor fd = null;
            try {
                fd = helper.openFile(uri, "r");
            } catch (DataAbilityRemoteException e) {
                e.printStackTrace();
            }

            ImageSource imageSource;
            imageSource = ImageSource.create(fd, null);

            // 设置图片参数
            ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
            decodingOptions.desiredSize = new Size(180, 180);
            imageSource.createPixelmap(decodingOptions);

            //创建ImagePacker对象
            ImagePacker imagePacker = ImagePacker.create();
            //设置编码选项
            ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
            packingOptions.format = "image/jpeg";
            //图像质量，范围从0-100，100为最佳质量。
            packingOptions.quality = 90;

            //该种方式直接访问internal://app目录
            FileOutputStream fos = new FileOutputStream(abilityContext.getFilesDir() + imgName);
            //初始化将结果输出到 OutputStream 对象的打包任务。
            boolean result = imagePacker.initializePacking(fos, packingOptions);
            if (result) {
                result = imagePacker.addImage(imageSource.createPixelmap(decodingOptions));
                if (result) {
                    long dataSize = imagePacker.finalizePacking();
                    HiLog.info(LABEL,"成功打包 图片大小：" + dataSize);
                    System.out.println(" APP LOG 成功打包 图片大小：" + dataSize);
                }
            }
            fos.flush();
            fos.close();
            saveImageToLibrary(name,imageSource.createPixelmap(decodingOptions));
        } catch (IOException e) {
            HiLog.error(LABEL,"文件保存出错：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        File file = abilityContext.getFilesDir();
        File[] files = file.listFiles();
        for (File file1 : files) {
            HiLog.info(LABEL,"File 目录下文件：" + file1);
            System.out.println(" APP LOG File目录:" + file1);
        }
        return true;
    }

    //保存图片到相册 fileName文件名  PixelMap 图片数据
    private void saveImageToLibrary(String fileName, PixelMap pixelMap) {
        try {
            ValuesBucket valuesBucket = new ValuesBucket();
            //文件名
            valuesBucket.putString(AVStorage.Images.Media.DISPLAY_NAME, fileName);
            //相对路径
            valuesBucket.putString("relative_path", "DCIM/");
            //文件格式，类型要一定要注意要是JPEG，PNG类型不支持
            valuesBucket.putString(AVStorage.Images.Media.MIME_TYPE, "image/JPEG");
            //应用独占：is_pending设置为1时表示只有该应用能访问此图片，其他应用无法发现该图片，当图片处理操作完成后再吧is_pending设置为0，解除独占，让其他应用可见
            valuesBucket.putInteger("is_pending", 1);

            DataAbilityHelper helper = DataAbilityHelper.creator(abilityContext);
            int index = helper.insert(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, valuesBucket);
            Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, String.valueOf(index));

            //获取到uri后，安卓通过contentResolver.openOutputStream(uri)就能获取到输出流来写文件，
            // 而鸿蒙没有提供这样的方法，我们就只能通过uri获取FileDescriptor，
            // 再通过FileDescriptor生成输出流打包编码成新的图片文件，
            // 这里helper.openFile方法一定要有“w”写模式，不然会报FileNotFound的错误。
            FileDescriptor fd = helper.openFile(uri, "w");

            //创建ImagePacker对象
            ImagePacker imagePacker = ImagePacker.create();

            //文件输出流
            OutputStream outputStream = new FileOutputStream(fd);
            //初始化将结果输出到 OutputStream 对象的打包任务。
            boolean result = imagePacker.initializePacking(outputStream, null);
            if (result) {
                //将pixelMap添加到编码器，进行编码
                result = imagePacker.addImage(pixelMap);
                if (result) {
                    long dataSize = imagePacker.finalizePacking();
                    System.out.println("APP LOG 成功打包 datasize"+dataSize);
                }
            }
            outputStream.flush();
            outputStream.close();
            valuesBucket.clear();
            //解除独占
            valuesBucket.putInteger("is_pending", 0);
            helper.update(uri, valuesBucket, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
