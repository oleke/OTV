package xyz.oleke.oleketv

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.database.DataSetObserver
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.*

import kotlinx.android.synthetic.main.activity_subscription.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Subscription : AppCompatActivity() {
    private val api = API()
    private var account: Account? = null
    private lateinit var plans:List<Model.SubscriptionPlan>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        loadPlans()
        btn_subscribe.setOnClickListener {
            subscribe()
        }
        account = intent.getSerializableExtra("account") as? Account

    }

    private fun showMain(account: Account){
        val main = Intent(this, MainActivity::class.java)
        main.putExtra("account",account)
        startActivity(main)
        finish()
    }


    private fun subscribe(){
        val request = api.newSubscription(account!!.getUser().id,(spinner_plans.selectedItem as Model.SubscriptionPlan?)!!.level,spinner_months.selectedItem.toString().toInt())
        GlobalScope.launch(Dispatchers.Main){
            val response = request.await()
            if(response!=null){
                showMain(Account(response))
            }
            else{
                Toast.makeText(baseContext,"Could not Subscribe",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadMonths(){
        ArrayAdapter.createFromResource(
            this,
            R.array.months,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner_months.adapter = adapter
        }

        spinner_months.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                subscription_plan_price.text = ((spinner_plans.selectedItem as? Model.SubscriptionPlan)!!.price * parent!!.getItemAtPosition(position).toString().toFloat()).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }

    private fun loadPlans(){
        val request = api.getPlans()
        GlobalScope.launch(Dispatchers.Main){
            plans = request.await()
            if(plans.isNotEmpty()){
                val adapter = ArrayAdapter<Model.SubscriptionPlan>(this@Subscription,android.R.layout.simple_spinner_item,plans)
                spinner_plans.adapter = adapter
                spinner_plans.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        subscription_plan_name.text = plans[position].name
                        subscription_plan_price.text = (plans[position].price * spinner_months.selectedItem.toString().toFloat()).toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                }
                loadMonths()
            }
        }
    }

}
