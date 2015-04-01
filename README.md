Android 中的 IOC 框架［ViewInject］

使用方法：

@ContentView(value=R.layout.activity_main)
public class MainActivity extends Activity {

	// id 名与控件名相同时可以忽略参数
	@ViewInject
	private TextView title,content;
	@ViewInject
	private EditText editText;
	
	// id 名与控件名不同时需指定 id 
	@ViewInject(value=R.id.submit)
	private Button bt_submit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtils.inject(this);
		
		title.setText("hello, zane");
		
	}
	
	@OnClick({R.id.submit, R.id.cancel})
	public void doClick(View view){
		switch (view.getId()) {
		case R.id.submit:
			Toast.makeText(this, "SUBMIT", Toast.LENGTH_SHORT).show();
			break;

		case R.id.cancel:
			Toast.makeText(this, "CANCEL", Toast.LENGTH_SHORT).show();
			break;
		
		default:
			break;
		}
	}

}
