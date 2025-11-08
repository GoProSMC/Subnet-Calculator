package com.example.subnetcalc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.subnetcalc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // CIDR 스피너 설정
        val cidrList = ArrayList<String>()
        for (i in 0..32) {
            cidrList.add("/$i")
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cidrList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCIDR.adapter = adapter
        binding.spinnerCIDR.setSelection(24)
        
        // 입력 감지
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculate()
            }
        }
        
        binding.etOctet1.addTextChangedListener(watcher)
        binding.etOctet2.addTextChangedListener(watcher)
        binding.etOctet3.addTextChangedListener(watcher)
        binding.etOctet4.addTextChangedListener(watcher)
        
        binding.spinnerCIDR.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                calculate()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
        
        binding.btnClear.setOnClickListener {
            binding.etOctet1.setText("")
            binding.etOctet2.setText("")
            binding.etOctet3.setText("")
            binding.etOctet4.setText("")
            binding.spinnerCIDR.setSelection(24)
            clearResults()
            binding.tvError.text = ""
        }
        
        binding.btnCopy.setOnClickListener {
            val text = binding.tvIpAddress.text.toString() + "\n" +
                    binding.tvSubnetMask.text.toString() + "\n" +
                    binding.tvCIDR.text.toString() + "\n" +
                    binding.tvNetworkAddress.text.toString() + "\n" +
                    binding.tvBroadcast.text.toString() + "\n" +
                    binding.tvFirstHost.text.toString() + "\n" +
                    binding.tvLastHost.text.toString() + "\n" +
                    binding.tvTotalHosts.text.toString() + "\n" +
                    binding.tvUsableHosts.text.toString() + "\n" +
                    binding.tvWildcard.text.toString() + "\n" +
                    binding.tvClass.text.toString() + "\n" +
                    binding.tvType.text.toString()
            
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("결과", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "복사됨", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun calculate() {
        try {
            val o1 = binding.etOctet1.text.toString()
            val o2 = binding.etOctet2.text.toString()
            val o3 = binding.etOctet3.text.toString()
            val o4 = binding.etOctet4.text.toString()
            
            if (o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty()) {
                return
            }
            
            val octet1 = o1.toInt()
            val octet2 = o2.toInt()
            val octet3 = o3.toInt()
            val octet4 = o4.toInt()
            
            if (octet1 < 0 || octet1 > 255 || octet2 < 0 || octet2 > 255 || 
                octet3 < 0 || octet3 > 255 || octet4 < 0 || octet4 > 255) {
                binding.tvError.text = "IP 주소는 0-255 범위여야 합니다"
                clearResults()
                return
            }
            
            val ip = "$octet1.$octet2.$octet3.$octet4"
            val cidr = binding.spinnerCIDR.selectedItemPosition
            
            // 계산
            val ipParts = listOf(octet1, octet2, octet3, octet4)
            val ipInt = (ipParts[0] shl 24) or (ipParts[1] shl 16) or (ipParts[2] shl 8) or ipParts[3]
            
            val maskInt = if (cidr == 0) 0 else (-1L shl (32 - cidr)).toInt()
            val mask = intToIp(maskInt)
            
            val networkInt = ipInt and maskInt
            val network = intToIp(networkInt)
            
            val wildcardInt = maskInt.inv()
            val broadcastInt = networkInt or wildcardInt
            val broadcast = intToIp(broadcastInt)
            
            val firstHost = if (cidr < 31) intToIp(networkInt + 1) else network
            val lastHost = if (cidr < 31) intToIp(broadcastInt - 1) else broadcast
            
            val totalHosts = Math.pow(2.0, (32 - cidr).toDouble()).toLong()
            val usableHosts = if (cidr < 31) totalHosts - 2 else if (cidr == 31) 2 else 1
            
            val wildcard = intToIp(wildcardInt)
            
            val binary = ipParts.joinToString(".") { 
                Integer.toBinaryString(it).padStart(8, '0')
            }
            
            val ipClass = when {
                octet1 in 1..126 -> "A"
                octet1 in 128..191 -> "B"
                octet1 in 192..223 -> "C"
                octet1 in 224..239 -> "D (멀티캐스트)"
                octet1 in 240..255 -> "E (예약됨)"
                else -> "알 수 없음"
            }
            
            val ipType = when {
                octet1 == 10 -> "사설 IP"
                octet1 == 172 && octet2 in 16..31 -> "사설 IP"
                octet1 == 192 && octet2 == 168 -> "사설 IP"
                octet1 == 127 -> "루프백"
                octet1 == 169 && octet2 == 254 -> "링크 로컬"
                octet1 in 224..239 -> "멀티캐스트"
                else -> "공인 IP"
            }
            
            // 결과 표시
            binding.tvIpAddress.text = "IP 주소: $ip"
            binding.tvSubnetMask.text = "서브넷 마스크: $mask"
            binding.tvCIDR.text = "CIDR: /$cidr"
            binding.tvNetworkAddress.text = "네트워크 주소: $network"
            binding.tvBroadcast.text = "브로드캐스트: $broadcast"
            binding.tvFirstHost.text = "첫 번째 호스트: $firstHost"
            binding.tvLastHost.text = "마지막 호스트: $lastHost"
            binding.tvTotalHosts.text = "총 호스트 수: $totalHosts"
            binding.tvUsableHosts.text = "사용 가능 호스트: $usableHosts"
            binding.tvWildcard.text = "와일드카드 마스크: $wildcard"
            binding.tvBinary.text = "이진수: $binary"
            binding.tvClass.text = "클래스: $ipClass"
            binding.tvType.text = "타입: $ipType"
            
            binding.tvError.text = ""
            
        } catch (e: Exception) {
            binding.tvError.text = "오류: ${e.message}"
            clearResults()
        }
    }
    
    fun intToIp(value: Int): String {
        val part1 = (value shr 24) and 0xFF
        val part2 = (value shr 16) and 0xFF
        val part3 = (value shr 8) and 0xFF
        val part4 = value and 0xFF
        return "$part1.$part2.$part3.$part4"
    }
    
    fun clearResults() {
        binding.tvIpAddress.text = ""
        binding.tvSubnetMask.text = ""
        binding.tvCIDR.text = ""
        binding.tvNetworkAddress.text = ""
        binding.tvBroadcast.text = ""
        binding.tvFirstHost.text = ""
        binding.tvLastHost.text = ""
        binding.tvTotalHosts.text = ""
        binding.tvUsableHosts.text = ""
        binding.tvWildcard.text = ""
        binding.tvBinary.text = ""
        binding.tvClass.text = ""
        binding.tvType.text = ""
    }
}
