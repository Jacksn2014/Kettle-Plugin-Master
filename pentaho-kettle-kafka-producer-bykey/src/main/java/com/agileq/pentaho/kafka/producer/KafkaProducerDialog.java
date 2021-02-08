package com.agileq.pentaho.kafka.producer;

import java.util.Properties;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
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
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * 微信公众号"以数据之名"
 * 
 * @ClassName: KafkaProducerDialog
 * @Description: UI for the Kafka Producer By Key step
 * @author: itbigbird
 * @date: 2019年8月10日 下午3:33:48
 */
public class KafkaProducerDialog extends BaseStepDialog implements
		StepDialogInterface {
	private KafkaProducerMeta producerMeta;
	private TextVar wTopicName;
	private CCombo wMessageField;
	private CCombo wKeyField;
	private TableView wProps;

	public KafkaProducerDialog(Shell parent, Object in, TransMeta tr,
			String sname) {
		super(parent, (BaseStepMeta) in, tr, sname);
		this.producerMeta = ((KafkaProducerMeta) in);
	}

	public KafkaProducerDialog(Shell parent, BaseStepMeta baseStepMeta,
			TransMeta transMeta, String stepname) {
		super(parent, baseStepMeta, transMeta, stepname);
		this.producerMeta = ((KafkaProducerMeta) baseStepMeta);
	}

	public KafkaProducerDialog(Shell parent, int nr, BaseStepMeta in,
			TransMeta tr) {
		super(parent, nr, in, tr);
		this.producerMeta = ((KafkaProducerMeta) in);
	}

	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		this.shell = new Shell(parent, 3312);
		this.props.setLook(this.shell);
		setShellImage(this.shell, this.producerMeta);

		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				KafkaProducerDialog.this.producerMeta.setChanged();
			}
		};
		this.changed = this.producerMeta.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 5;
		formLayout.marginHeight = 5;

		this.shell.setLayout(formLayout);
		this.shell.setText(Messages
				.getString("KafkaProducerDialog.Shell.Title"));

		int middle = this.props.getMiddlePct();
		int margin = 4;

		this.wlStepname = new Label(this.shell, 131072);
		this.wlStepname.setText(Messages
				.getString("KafkaProducerDialog.StepName.Label"));
		this.props.setLook(this.wlStepname);
		this.fdlStepname = new FormData();
		this.fdlStepname.left = new FormAttachment(0, 0);
		this.fdlStepname.right = new FormAttachment(middle, -margin);
		this.fdlStepname.top = new FormAttachment(0, margin);
		this.wlStepname.setLayoutData(this.fdlStepname);
		this.wStepname = new Text(this.shell, 18436);
		this.props.setLook(this.wStepname);
		this.wStepname.addModifyListener(lsMod);
		this.fdStepname = new FormData();
		this.fdStepname.left = new FormAttachment(middle, 0);
		this.fdStepname.top = new FormAttachment(0, margin);
		this.fdStepname.right = new FormAttachment(100, 0);
		this.wStepname.setLayoutData(this.fdStepname);
		Control lastControl = this.wStepname;

		Label wlTopicName = new Label(this.shell, 131072);
		wlTopicName.setText(Messages
				.getString("KafkaProducerDialog.TopicName.Label"));
		this.props.setLook(wlTopicName);
		FormData fdlTopicName = new FormData();
		fdlTopicName.top = new FormAttachment(lastControl, margin);
		fdlTopicName.left = new FormAttachment(0, 0);
		fdlTopicName.right = new FormAttachment(middle, -margin);
		wlTopicName.setLayoutData(fdlTopicName);
		this.wTopicName = new TextVar(this.transMeta, this.shell, 18436);
		this.props.setLook(this.wTopicName);
		this.wTopicName.addModifyListener(lsMod);
		FormData fdTopicName = new FormData();
		fdTopicName.top = new FormAttachment(lastControl, margin);
		fdTopicName.left = new FormAttachment(middle, 0);
		fdTopicName.right = new FormAttachment(100, 0);
		this.wTopicName.setLayoutData(fdTopicName);
		lastControl = this.wTopicName;
		RowMetaInterface previousFields;
		try {
			previousFields = this.transMeta.getPrevStepFields(this.stepMeta);
		} catch (KettleStepException e) {
			new ErrorDialog(
					this.shell,
					BaseMessages.getString("System.Dialog.Error.Title"),
					Messages.getString("KafkaProducerDialog.ErrorDialog.UnableToGetInputFields"),
					e);

			previousFields = new RowMeta();
		}
		Label wlMessageField = new Label(this.shell, 131072);
		wlMessageField.setText(Messages
				.getString("KafkaProducerDialog.MessageFieldName.Label"));
		this.props.setLook(wlMessageField);
		FormData fdlMessageField = new FormData();
		fdlMessageField.top = new FormAttachment(lastControl, margin);
		fdlMessageField.left = new FormAttachment(0, 0);
		fdlMessageField.right = new FormAttachment(middle, -margin);
		wlMessageField.setLayoutData(fdlMessageField);
		this.wMessageField = new CCombo(this.shell, 18436);
		this.wMessageField.setItems(previousFields.getFieldNames());
		this.props.setLook(this.wMessageField);
		this.wMessageField.addModifyListener(lsMod);
		FormData fdMessageField = new FormData();
		fdMessageField.top = new FormAttachment(lastControl, margin);
		fdMessageField.left = new FormAttachment(middle, 0);
		fdMessageField.right = new FormAttachment(100, 0);
		this.wMessageField.setLayoutData(fdMessageField);
		lastControl = this.wMessageField;

		Label wlKeyField = new Label(this.shell, 131072);
		wlKeyField.setText(Messages
				.getString("KafkaProducerDialog.KeyFieldName.Label"));
		this.props.setLook(wlKeyField);
		FormData fdlKeyField = new FormData();
		fdlKeyField.top = new FormAttachment(lastControl, margin);
		fdlKeyField.left = new FormAttachment(0, 0);
		fdlKeyField.right = new FormAttachment(middle, -margin);
		wlKeyField.setLayoutData(fdlKeyField);
		this.wKeyField = new CCombo(this.shell, 18436);
		this.wKeyField.setItems(previousFields.getFieldNames());
		this.props.setLook(this.wKeyField);
		this.wKeyField.addModifyListener(lsMod);
		FormData fdKeyField = new FormData();
		fdKeyField.top = new FormAttachment(lastControl, margin);
		fdKeyField.left = new FormAttachment(middle, 0);
		fdKeyField.right = new FormAttachment(100, 0);
		this.wKeyField.setLayoutData(fdKeyField);
		lastControl = this.wKeyField;

		this.wOK = new Button(this.shell, 8);
		this.wOK.setText(BaseMessages.getString("System.Button.OK"));
		this.wCancel = new Button(this.shell, 8);
		this.wCancel.setText(BaseMessages.getString("System.Button.Cancel"));

		setButtonPositions(new Button[] { this.wOK, this.wCancel }, margin,
				null);

		ColumnInfo[] colinf = {
				new ColumnInfo(
						Messages.getString("KafkaProducerDialog.TableView.NameCol.Label"),
						1, false),
				new ColumnInfo(
						Messages.getString("KafkaProducerDialog.TableView.ValueCol.Label"),
						1, false) };

		this.wProps = new TableView(this.transMeta, this.shell, 65538, colinf,
				1, lsMod, this.props);
		FormData fdProps = new FormData();
		fdProps.top = new FormAttachment(lastControl, margin * 2);
		fdProps.bottom = new FormAttachment(this.wOK, -margin * 2);
		fdProps.left = new FormAttachment(0, 0);
		fdProps.right = new FormAttachment(100, 0);
		this.wProps.setLayoutData(fdProps);

		this.lsCancel = new Listener() {
			public void handleEvent(Event e) {
				KafkaProducerDialog.this.cancel();
			}
		};
		this.lsOK = new Listener() {
			public void handleEvent(Event e) {
				KafkaProducerDialog.this.ok();
			}
		};
		this.wCancel.addListener(13, this.lsCancel);
		this.wOK.addListener(13, this.lsOK);

		this.lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				KafkaProducerDialog.this.ok();
			}
		};
		this.wStepname.addSelectionListener(this.lsDef);
		this.wTopicName.addSelectionListener(this.lsDef);
		this.wMessageField.addSelectionListener(this.lsDef);
		this.wKeyField.addSelectionListener(this.lsDef);

		this.shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				KafkaProducerDialog.this.cancel();
			}
		});
		setSize(this.shell, 400, 350, true);

		getData(this.producerMeta, true);
		this.producerMeta.setChanged(this.changed);

		this.shell.open();
		while (!this.shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return this.stepname;
	}

	private void getData(KafkaProducerMeta producerMeta, boolean copyStepname) {
		if (copyStepname) {
			this.wStepname.setText(this.stepname);
		}
		this.wTopicName.setText(Const.NVL(producerMeta.getTopic(), ""));
		this.wMessageField
				.setText(Const.NVL(producerMeta.getMessageField(), ""));
		this.wKeyField.setText(Const.NVL(producerMeta.getKeyField(), ""));

		Properties kafkaProperties = producerMeta.getKafkaProperties();
		for (int i = 0; i < KafkaProducerMeta.KAFKA_PROPERTIES_NAMES.length; i++) {
			String propName = KafkaProducerMeta.KAFKA_PROPERTIES_NAMES[i];
			String value = kafkaProperties.getProperty(propName);
			TableItem item = new TableItem(this.wProps.table, i > 1 ? 1 : 0);
			int colnr = 1;
			item.setText(colnr++, Const.NVL(propName, ""));
			String defaultValue = (String) KafkaProducerMeta.KAFKA_PROPERTIES_DEFAULTS
					.get(propName);
			if (defaultValue == null) {
				defaultValue = "(default)";
			}
			item.setText(colnr++, Const.NVL(value, defaultValue));
		}
		this.wProps.removeEmptyRows();
		this.wProps.setRowNums();
		this.wProps.optWidth(true);

		this.wStepname.selectAll();
	}

	private void cancel() {
		this.stepname = null;
		this.producerMeta.setChanged(this.changed);
		dispose();
	}

	private void setData(KafkaProducerMeta producerMeta) {
		producerMeta.setTopic(this.wTopicName.getText());
		producerMeta.setMessageField(this.wMessageField.getText());
		producerMeta.setKeyField(this.wKeyField.getText());

		Properties kafkaProperties = producerMeta.getKafkaProperties();
		int nrNonEmptyFields = this.wProps.nrNonEmpty();
		for (int i = 0; i < nrNonEmptyFields; i++) {
			TableItem item = this.wProps.getNonEmpty(i);
			int colnr = 1;
			String name = item.getText(colnr++);
			String value = item.getText(colnr++).trim();
			if ((value.length() > 0) && (!"(default)".equals(value))) {
				kafkaProperties.put(name, value);
			} else {
				kafkaProperties.remove(name);
			}
		}
		this.wProps.removeEmptyRows();
		this.wProps.setRowNums();
		this.wProps.optWidth(true);

		producerMeta.setChanged();
	}

	private void ok() {
		if (Const.isEmpty(this.wStepname.getText())) {
			return;
		}
		setData(this.producerMeta);
		this.stepname = this.wStepname.getText();
		dispose();
	}
}
