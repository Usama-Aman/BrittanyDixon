package com.cp.brittany.dixon.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.databinding.AcitivityImagePickerBinding
import com.cp.brittany.dixon.utils.PhotoUtil
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio


class ImagePickerActivity : BaseActivity() {

    private lateinit var binding: AcitivityImagePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcitivityImagePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getImage()
    }

    private fun getImage() {
        val options = Options().apply {
            ratio = Ratio.RATIO_AUTO                        //Image/video capture ratio
            count = 1                                       //Number of images to restrict selection count
            spanCount = 4                                   //Number for columns in grid
            path = "Pix/Camera"                             //Custom Path For media Storage
            isFrontFacing = false                           //Front Facing camera on start
            mode = Mode.Picture                             //Option to select only pictures or videos or both
            flash = Flash.Auto                              //Option to select flash type
        }

        addPixToActivity(R.id.container, options) {
            when (it.status) {
                PixEventCallback.Status.SUCCESS -> {
                    val mPath = it.data
                    if (!mPath.isNullOrEmpty())
                        if (mPath.isNotEmpty()) {
                            val resultIntent = Intent()
                            val filePath = PhotoUtil.getPath(this@ImagePickerActivity, mPath[0])
                            resultIntent.putExtra("filePath", filePath)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                }
                PixEventCallback.Status.BACK_PRESSED -> {
                    Toast.makeText(this@ImagePickerActivity, "Back", Toast.LENGTH_SHORT).show()
                    supportFragmentManager.popBackStack()
                }
            }
        }
    }


}