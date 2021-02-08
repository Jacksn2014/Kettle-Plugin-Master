package com.agileq.kettle.mts.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * 微信公众号"以数据之名"
 * @Title: MQ To SQL
 * @ClassName: MqToSqlDialog 
 * @Description: 对话框类
 * @author: itbigbird
 * @date: 2017-04-12 下午07:10:26
 * @version: V1.0
 */

public class MqToSqlDialog extends BaseStepDialog implements
		StepDialogInterface {

	private static Class<?> PKG = MqToSqlDialog.class; // for i18n purposes

	private MqToSqlMeta input;

	private CCombo wJsonStr;

	private Text wJsonKeyStr;

	private Text wJsonValueStr;

	private Text wTableName;

	private Text wPrimaryKey;

	private CCombo wOperType;

	private CCombo wJsonDefaultStr;

	private Text wOutputDML;

	public MqToSqlDialog(Shell parent, Object in, TransMeta transMeta,
			String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (MqToSqlMeta) in;
	}

	public MqToSqlDialog(Shell parent, BaseStepMeta baseStepMeta,
			TransMeta transMeta, String stepname) {
		super(parent, baseStepMeta, transMeta, stepname);
		input = (MqToSqlMeta) baseStepMeta;
	}

	public MqToSqlDialog(Shell parent, int nr, BaseStepMeta in,
			TransMeta tr) {
		super(parent, nr, in, tr);
		input = (MqToSqlMeta) in;
	}

	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN
				| SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, input);

		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				input.setChanged();
			}
		};
		changed = input.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG,
				"MqToSqlDialog.Shell.Title"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Stepname line
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG,
				"MqToSqlDialog.StepName.Label"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);

		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		//
		RowMetaInterface previousFields;
		try {
			previousFields = transMeta.getPrevStepFields(stepMeta);
		} catch (KettleStepException e) {
			new ErrorDialog(
					shell,
					BaseMessages.getString("System.Dialog.Error.Title"),
					Messages.getString("MqToSqlDialog.ErrorDialog.UnableToGetInputFields"),
					e);

			previousFields = new RowMeta();
		}
		// 1、待解析的jsonStr的标签和下拉列别
		Label wlJsonStr = new Label(shell, SWT.RIGHT);
		wlJsonStr.setText(BaseMessages.getString(PKG,
				"MqToSqlDialog.JsonStr.Label"));
		props.setLook(wlJsonStr);
		FormData fdlJsonStr = new FormData();
		fdlJsonStr.left = new FormAttachment(0, 0);
		fdlJsonStr.right = new FormAttachment(middle, -margin);
		fdlJsonStr.top = new FormAttachment(5, margin);
		wlJsonStr.setLayoutData(fdlJsonStr);

		wJsonStr = new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wJsonStr.setItems(previousFields.getFieldNames());
		props.setLook(wJsonStr);
		wJsonStr.addModifyListener(lsMod);
		FormData fdJsonStr = new FormData();
		fdJsonStr.left = new FormAttachment(middle, 0);
		fdJsonStr.right = new FormAttachment(100, 0);
		fdJsonStr.top = new FormAttachment(5, margin);
		wJsonStr.setLayoutData(fdJsonStr);

		// 2、待解析的jsonKeyStr的标签和文本框
		Label wlJsonKeyStr = new Label(shell, SWT.RIGHT);
		wlJsonKeyStr.setText(BaseMessages.getString(PKG,
				"MqToSqlDialog.JsonKeyStr.Label"));
		props.setLook(wlJsonKeyStr);
		FormData fdlJsonKeyStr = new FormData();
		fdlJsonKeyStr.top = new FormAttachment(10, margin);
		fdlJsonKeyStr.left = new FormAttachment(0, 0);
		fdlJsonKeyStr.right = new FormAttachment(middle, -margin);
		wlJsonKeyStr.setLayoutData(fdlJsonKeyStr);

		wJsonKeyStr = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(this.wJsonKeyStr);
		wJsonKeyStr.addModifyListener(lsMod);
		FormData fdJsonKeyStr = new FormData();
		fdJsonKeyStr.top = new FormAttachment(10, margin);
		fdJsonKeyStr.left = new FormAttachment(middle, 0);
		fdJsonKeyStr.right = new FormAttachment(100, 0);
		wJsonKeyStr.setLayoutData(fdJsonKeyStr);

		// 3、待解析的jsonValueStr的标签和文本框
		Label wlJsonValueStr = new Label(shell, SWT.RIGHT);
		wlJsonValueStr.setText(BaseMessages.getString(PKG,
				"MqToSqlDialog.JsonValueStr.Label"));
		props.setLook(wlJsonValueStr);
		FormData fdlJsonValueStr = new FormData();
		fdlJsonValueStr.top = new FormAttachment(15, margin);
		fdlJsonValueStr.left = new FormAttachment(0, 0);
		fdlJsonValueStr.right = new FormAttachment(middle, -margin);
		wlJsonValueStr.setLayoutData(fdlJsonValueStr);

		wJsonValueStr = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wJsonValueStr);
		wJsonValueStr.addModifyListener(lsMod);
		FormData fdJsonValueStr = new FormData();
		fdJsonValueStr.top = new FormAttachment(15, margin);
		fdJsonValueStr.left = new FormAttachment(middle, 0);
		fdJsonValueStr.right = new FormAttachment(100, 0);
		wJsonValueStr.setLayoutData(fdJsonValueStr);

		// 4、待解析的tableName的标签和文本框
		Label wlTableName = new Label(shell, SWT.RIGHT);
		wlTableName.setText(BaseMessages.getString(PKG,
				"MqToSqlDialog.TableName.Label"));
		props.setLook(wlTableName);
		FormData fdlTableName = new FormData();
		fdlTableName.top = new FormAttachment(20, margin);
		fdlTableName.left = new FormAttachment(0, 0);
		fdlTableName.right = new FormAttachment(middle, -margin);
		wlTableName.setLayoutData(fdlTableName);

		wTableName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wTableName);
		wTableName.addModifyListener(lsMod);
		FormData fdTableName = new FormData();
		fdTableName.top = new FormAttachment(20, margin);
		fdTableName.left = new FormAttachment(middle, 0);
		fdTableName.right = new FormAttachment(100, 0);
		wTableName.setLayoutData(fdTableName);

		// 5、待解析的primaryKey的标签和文本框
		Label wlPrimaryKey = new Label(shell, SWT.RIGHT);
		wlPrimaryKey.setText(BaseMessages.getString(PKG,
				"MqToSqlDialog.PrimaryKey.Label"));
		props.setLook(wlPrimaryKey);
		FormData fdlPrimaryKey = new FormData();
		fdlPrimaryKey.top = new FormAttachment(25, margin);
		fdlPrimaryKey.left = new FormAttachment(0, 0);
		fdlPrimaryKey.right = new FormAttachment(middle, -margin);
		wlPrimaryKey.setLayoutData(fdlPrimaryKey);

		wPrimaryKey = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wPrimaryKey);
		wPrimaryKey.addModifyListener(lsMod);
		FormData fdPrimaryKey = new FormData();
		fdPrimaryKey.top = new FormAttachment(25, margin);
		fdPrimaryKey.left = new FormAttachment(middle, 0);
		fdPrimaryKey.right = new FormAttachment(100, 0);
		wPrimaryKey.setLayoutData(fdPrimaryKey);

		// 6、待解析的OperType的标签和下拉列别
		Label wlOperType = new Label(shell, SWT.RIGHT);
		wlOperType.setText(BaseMessages.getString(PKG,
				"MqToSqlDialog.OperType.Label"));
		props.setLook(wlOperType);
		FormData fdlOperType = new FormData();
		fdlOperType.left = new FormAttachment(0, 0);
		fdlOperType.right = new FormAttachment(middle, -margin);
		fdlOperType.top = new FormAttachment(30, margin);
		wlOperType.setLayoutData(fdlOperType);

		wOperType = new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wOperType.setItems(previousFields.getFieldNames());
		props.setLook(wOperType);
		wOperType.addModifyListener(lsMod);
		FormData fdOperType = new FormData();
		fdOperType.left = new FormAttachment(middle, 0);
		fdOperType.right = new FormAttachment(100, 0);
		fdOperType.top = new FormAttachment(30, margin);
		wOperType.setLayoutData(fdOperType);

		// 7、待解析的JsonDefaultStr的标签和下拉列别
		Label wlJsonDefaultStr = new Label(shell, SWT.RIGHT);
		wlJsonDefaultStr.setText(BaseMessages.getString(PKG,
				"MqToSqlDialog.JsonDefaultStr.Label"));
		props.setLook(wlJsonDefaultStr);
		FormData fdlJsonDefaultStr = new FormData();
		fdlJsonDefaultStr.top = new FormAttachment(35, margin);
		fdlJsonDefaultStr.left = new FormAttachment(0, 0);
		fdlJsonDefaultStr.right = new FormAttachment(middle, -margin);
		wlJsonDefaultStr.setLayoutData(fdlJsonDefaultStr);

		wJsonDefaultStr = new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wJsonDefaultStr.setItems(previousFields.getFieldNames());
		props.setLook(wJsonDefaultStr);
		wJsonDefaultStr.addModifyListener(lsMod);
		FormData fdJsonDefaultStr = new FormData();
		fdJsonDefaultStr.top = new FormAttachment(35, margin);
		fdJsonDefaultStr.left = new FormAttachment(middle, 0);
		fdJsonDefaultStr.right = new FormAttachment(100, 0);
		wJsonDefaultStr.setLayoutData(fdJsonDefaultStr);

		// 8、待输出的outputDML的标签和文本框
		Label wlOutputDML = new Label(shell, SWT.RIGHT);
		wlOutputDML.setText(BaseMessages.getString(PKG,
				"MqToSqlDialog.OutputDML.Label"));
		props.setLook(wlOutputDML);
		FormData fdlOutputDML = new FormData();
		fdlOutputDML.top = new FormAttachment(40, margin);
		fdlOutputDML.left = new FormAttachment(0, 0);
		fdlOutputDML.right = new FormAttachment(middle, -margin);
		wlOutputDML.setLayoutData(fdlOutputDML);

		wOutputDML = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wOutputDML);
		wJsonDefaultStr.addModifyListener(lsMod);
		FormData fdOutputDML = new FormData();
		fdOutputDML.top = new FormAttachment(40, margin);
		fdOutputDML.left = new FormAttachment(middle, 0);
		fdOutputDML.right = new FormAttachment(100, 0);
		wOutputDML.setLayoutData(fdOutputDML);

		// OK and cancel buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		/*
		 * BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK,
		 * wCancel }, margin, null);
		 */
		setButtonPositions(new Button[] { wOK, wCancel }, margin, null);

		// Add listeners
		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok(input);
			}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok(input);
			}
		};

		wStepname.addSelectionListener(lsDef);
		wJsonStr.addSelectionListener(lsDef);
		wJsonKeyStr.addSelectionListener(lsDef);
		wJsonValueStr.addSelectionListener(lsDef);
		wTableName.addSelectionListener(lsDef);
		wPrimaryKey.addSelectionListener(lsDef);
		wOperType.addSelectionListener(lsDef);
		wJsonDefaultStr.addSelectionListener(lsDef);
		wOutputDML.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		// Set the shell size, based upon previous time...
		setSize(shell, 600, 550, true);

		getData(input, true);
		input.setChanged(changed);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return stepname;
	}

	// Read data and place it in the dialog
	public void getData(MqToSqlMeta input, boolean copyStepname) {
		if (copyStepname) {
			wStepname.setText(stepname);
		}
		wJsonStr.setText(Const.NVL(input.getJsonStr(), ""));
		wJsonKeyStr.setText(Const.NVL(input.getJsonKeyStr(), ""));
		wJsonValueStr.setText(Const.NVL(input.getJsonValueStr(), ""));
		wTableName.setText(Const.NVL(input.getTableName(), ""));
		wPrimaryKey.setText(Const.NVL(input.getPrimaryKey(), ""));
		wOperType.setText(Const.NVL(input.getOperType(), ""));
		wJsonDefaultStr.setText(Const.NVL(input.getJsonDefaultStr(), ""));
		wOutputDML.setText(Const.NVL(input.getOutputDML(), ""));

		wStepname.selectAll();
	}

	private void cancel() {
		stepname = null;
		input.setChanged(changed);
		dispose();
	}

	// let the plugin know about the entered data
	private void ok(MqToSqlMeta input) {
		stepname = wStepname.getText(); // return value
		input.setJsonStr(wJsonStr.getText());
		input.setJsonKeyStr(wJsonKeyStr.getText());
		input.setJsonValueStr(wJsonValueStr.getText());
		input.setTableName(wTableName.getText());
		input.setPrimaryKey(wPrimaryKey.getText());
		input.setOperType(wOperType.getText());
		input.setJsonDefaultStr(wJsonDefaultStr.getText());
		input.setOutputDML(wOutputDML.getText());
		dispose();
	}
}
