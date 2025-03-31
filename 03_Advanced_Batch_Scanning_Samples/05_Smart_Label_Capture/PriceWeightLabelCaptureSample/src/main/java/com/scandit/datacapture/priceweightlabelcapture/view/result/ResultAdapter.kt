/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scandit.datacapture.priceweightlabelcapture.view.result

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scandit.datacapture.priceweightlabelcapture.R
import com.scandit.datacapture.priceweightlabelcapture.data.ScannedResult
import com.scandit.datacapture.priceweightlabelcapture.databinding.ItemResultBinding

class ResultAdapter(results: List<ScannedResult> = emptyList()) :
    RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<ScannedResult>() {
        override fun areItemsTheSame(oldItem: ScannedResult, newItem: ScannedResult): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ScannedResult, newItem: ScannedResult): Boolean {
            return oldItem == newItem
        }
    }
    private val asyncListDiffer = AsyncListDiffer(this, diffUtil).also {
        it.submitList(results)
    }

    fun setResults(results: List<ScannedResult>) {
        asyncListDiffer.submitList(results)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemResultBinding.inflate(inflater, parent, false)
        return ResultViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(position, asyncListDiffer.currentList[position])
    }

    class ResultViewHolder(private val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, result: ScannedResult) {
            when (result) {
                is ScannedResult.Barcode -> bindBarcode(result)
                is ScannedResult.Label -> bindLabel(result)
            }
            binding.itemNumber.text = binding.root.context.getString(R.string.item_x, position + 1)
            binding.quantityLabel.isVisible = result.quantity > 1
            binding.quantityLabel.text =
                binding.root.context.getString(R.string.item_quantity, result.quantity)
        }

        private fun bindBarcode(result: ScannedResult.Barcode) {
            binding.upcContent.text = result.data
            binding.labelGroup.visibility = View.GONE
        }

        private fun bindLabel(result: ScannedResult.Label) {
            binding.upcContent.text = result.data
            binding.weightContent.text = result.weight
            binding.unitPriceContent.text = result.unitPrice
            binding.labelGroup.visibility = View.VISIBLE
        }
    }
}
