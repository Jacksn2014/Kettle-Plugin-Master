package com.agileq.kettle.splunk;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransPreviewFactory;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.EnterNumberDialog;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.dialog.PreviewRowsDialog;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.dialog.TransPreviewProgressDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import com.agileq.kettle.splunk.connection.SplunkConnection;
import com.splunk.Args;
import com.splunk.JobArgs;
import com.splunk.ResultsReaderXml;
import com.splunk.Service;

/**
 * 微信公众号"以数据之名"
 * @author: itbigbird
 * @ClassName: SplunkInputDialog
 * @Description: define plugin dialog 
 * @date: 2019年8月18日 下午2:33:48
 */
public class SplunkInputDialog extends BaseStepDialog implements
		StepDialogInterface {

	private static Class<?> PKG = SplunkInputMeta.class; // for i18n purposes,
															// needed by
															// Translator2!!

	private Text wStepname;

	private Text wHost;

	private Text wPort;

	private Text wUsername;

	private Text wPassword;

	private Text wQuery;

	private TableView wReturns;

	private SplunkInputMeta input;

	public SplunkInputDialog(Shell parent, Object inputMetadata,
			TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) inputMetadata, transMeta, stepname);
		input = (SplunkInputMeta) inputMetadata;

	}

	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX
				| SWT.MIN);
		props.setLook(shell);
		setShellImage(shell, input);

		FormLayout shellLayout = new FormLayout();
		shell.setLayout(shellLayout);
		shell.setText("Splunk Input");

		// 1.8
		// ModifyListener lsMod = e -> input.setChanged();
		// 1.7
		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				input.setChanged();
			}
		};
		changed = input.hasChanged();

		ScrolledComposite wScrolledComposite = new ScrolledComposite(shell,
				SWT.V_SCROLL | SWT.H_SCROLL);
		FormLayout scFormLayout = new FormLayout();
		wScrolledComposite.setLayout(scFormLayout);
		FormData fdSComposite = new FormData();
		fdSComposite.left = new FormAttachment(0, 0);
		fdSComposite.right = new FormAttachment(100, 0);
		fdSComposite.top = new FormAttachment(0, 0);
		fdSComposite.bottom = new FormAttachment(100, 0);
		wScrolledComposite.setLayoutData(fdSComposite);

		Composite wComposite = new Composite(wScrolledComposite, SWT.NONE);
		props.setLook(wComposite);
		FormData fdComposite = new FormData();
		fdComposite.left = new FormAttachment(0, 0);
		fdComposite.right = new FormAttachment(100, 0);
		fdComposite.top = new FormAttachment(0, 0);
		fdComposite.bottom = new FormAttachment(100, 0);
		wComposite.setLayoutData(fdComposite);

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;
		wComposite.setLayout(formLayout);

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Step name line
		//
		Label wlStepname = new Label(wComposite, SWT.RIGHT);
		wlStepname.setText("Step name");
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname = new Text(wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(wlStepname, 0, SWT.CENTER);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		Control lastControl = wStepname;

		Label wlHost = new Label(wComposite, SWT.RIGHT);
		wlHost.setText("Splunk Host");
		props.setLook(wlHost);
		FormData fdlHost = new FormData();
		fdlHost.left = new FormAttachment(0, 0);
		fdlHost.right = new FormAttachment(middle, -margin);
		fdlHost.top = new FormAttachment(lastControl, 2 * margin);
		wlHost.setLayoutData(fdlHost);

		wHost = new Text(wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wHost);
		wHost.addModifyListener(lsMod);
		FormData fdHost = new FormData();
		fdHost.left = new FormAttachment(middle, 0);
		fdHost.right = new FormAttachment(100, 0);
		fdHost.top = new FormAttachment(wlHost, 0, SWT.CENTER);
		wHost.setLayoutData(fdHost);
		lastControl = wHost;

		Label wlPort = new Label(wComposite, SWT.RIGHT);
		wlPort.setText("Splunk Port");
		props.setLook(wlPort);
		FormData fdlPort = new FormData();
		fdlPort.left = new FormAttachment(0, 0);
		fdlPort.right = new FormAttachment(middle, -margin);
		fdlPort.top = new FormAttachment(lastControl, 2 * margin);
		wlPort.setLayoutData(fdlPort);

		wPort = new Text(wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wPort);
		wPort.addModifyListener(lsMod);
		FormData fdPort = new FormData();
		fdPort.left = new FormAttachment(middle, 0);
		fdPort.right = new FormAttachment(100, 0);
		fdPort.top = new FormAttachment(wlPort, 0, SWT.CENTER);
		wPort.setLayoutData(fdPort);
		lastControl = wPort;

		Label wlUsername = new Label(wComposite, SWT.RIGHT);
		wlUsername.setText("Splunk Username");
		props.setLook(wlUsername);
		FormData fdlUsername = new FormData();
		fdlUsername.left = new FormAttachment(0, 0);
		fdlUsername.right = new FormAttachment(middle, -margin);
		fdlUsername.top = new FormAttachment(lastControl, 2 * margin);
		wlUsername.setLayoutData(fdlUsername);

		wUsername = new Text(wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wUsername);
		wUsername.addModifyListener(lsMod);
		FormData fdUsername = new FormData();
		fdUsername.left = new FormAttachment(middle, 0);
		fdUsername.right = new FormAttachment(100, 0);
		fdUsername.top = new FormAttachment(wlUsername, 0, SWT.CENTER);
		wUsername.setLayoutData(fdUsername);
		lastControl = wUsername;

		Label wlPassword = new Label(wComposite, SWT.RIGHT);
		wlPassword.setText("Splunk Password");
		props.setLook(wlPassword);
		FormData fdlPassword = new FormData();
		fdlPassword.left = new FormAttachment(0, 0);
		fdlPassword.right = new FormAttachment(middle, -margin);
		fdlPassword.top = new FormAttachment(lastControl, 2 * margin);
		wlPassword.setLayoutData(fdlPassword);

		wPassword = new Text(wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wPassword);
		wPassword.addModifyListener(lsMod);
		FormData fdPassword = new FormData();
		fdPassword.left = new FormAttachment(middle, 0);
		fdPassword.right = new FormAttachment(100, 0);
		fdPassword.top = new FormAttachment(wlPassword, 0, SWT.CENTER);
		wPassword.setLayoutData(fdPassword);
		lastControl = wPassword;

		Label wlQuery = new Label(wComposite, SWT.LEFT);
		wlQuery.setText("Query:");
		props.setLook(wlQuery);
		FormData fdlQuery = new FormData();
		fdlQuery.left = new FormAttachment(0, 0);
		fdlQuery.right = new FormAttachment(middle, -margin);
		fdlQuery.top = new FormAttachment(lastControl, margin);
		wlQuery.setLayoutData(fdlQuery);
		wQuery = new Text(wComposite, SWT.MULTI | SWT.LEFT | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		wQuery.setFont(GUIResource.getInstance().getFontFixed());
		props.setLook(wQuery);
		wQuery.addModifyListener(lsMod);
		FormData fdQuery = new FormData();
		fdQuery.left = new FormAttachment(0, 0);
		fdQuery.right = new FormAttachment(100, 0);
		fdQuery.top = new FormAttachment(wlQuery, margin);
		fdQuery.bottom = new FormAttachment(60, 0);
		wQuery.setLayoutData(fdQuery);
		lastControl = wQuery;

		// Some buttons
		wOK = new Button(wComposite, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wPreview = new Button(wComposite, SWT.PUSH);
		wPreview.setText(BaseMessages.getString(PKG, "System.Button.Preview"));
		Button wTest = new Button(wComposite, SWT.PUSH);
		wTest.setText(BaseMessages.getString(PKG, "System.Button.Test"));
		wCancel = new Button(wComposite, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		// Position the buttons at the bottom of the dialog.
		//
		setButtonPositions(new Button[] { wOK, wPreview, wTest, wCancel },
				margin, null);

		// Table: return field name and type
		//
		ColumnInfo[] returnColumns = new ColumnInfo[] {
				new ColumnInfo("Field name", ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo("Splunk name", ColumnInfo.COLUMN_TYPE_TEXT,
						false),
				new ColumnInfo("Return type", ColumnInfo.COLUMN_TYPE_CCOMBO,
						ValueMetaFactory.getAllValueMetaNames(), false),
				new ColumnInfo("Length", ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo("Format", ColumnInfo.COLUMN_TYPE_TEXT, false), };

		Label wlReturns = new Label(wComposite, SWT.LEFT);
		wlReturns.setText("Returns");
		props.setLook(wlReturns);
		FormData fdlReturns = new FormData();
		fdlReturns.left = new FormAttachment(0, 0);
		fdlReturns.right = new FormAttachment(middle, -margin);
		fdlReturns.top = new FormAttachment(lastControl, margin);
		wlReturns.setLayoutData(fdlReturns);

		Button wbGetReturnFields = new Button(wComposite, SWT.PUSH);
		wbGetReturnFields.setText("Get Output Fields");
		FormData fdbGetReturnFields = new FormData();
		fdbGetReturnFields.right = new FormAttachment(100, 0);
		fdbGetReturnFields.top = new FormAttachment(wlReturns, margin);
		wbGetReturnFields.setLayoutData(fdbGetReturnFields);
		// 1.8
		// wbGetReturnFields.addListener( SWT.Selection, ( e ) ->
		// getReturnValues() );
		// 1.7
		wbGetReturnFields.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				getReturnValues();
			}
		});

		wReturns = new TableView(transMeta, wComposite, SWT.FULL_SELECTION
				| SWT.MULTI, returnColumns, input.getReturnValues().size(),
				lsMod, props);
		props.setLook(wReturns);
		wReturns.addModifyListener(lsMod);
		FormData fdReturns = new FormData();
		fdReturns.left = new FormAttachment(0, 0);
		fdReturns.right = new FormAttachment(wbGetReturnFields, 0);
		fdReturns.top = new FormAttachment(wlReturns, margin);
		fdReturns.bottom = new FormAttachment(wlReturns, 300 + margin);
		wReturns.setLayoutData(fdReturns);
		// lastControl = wReturns;

		wComposite.pack();
		Rectangle bounds = wComposite.getBounds();

		wScrolledComposite.setContent(wComposite);

		wScrolledComposite.setExpandHorizontal(true);
		wScrolledComposite.setExpandVertical(true);
		wScrolledComposite.setMinWidth(bounds.width);
		wScrolledComposite.setMinHeight(bounds.height);

		// Add listeners 1.8
		// wCancel.addListener( SWT.Selection, e -> cancel() );
		// wOK.addListener( SWT.Selection, e -> ok() );
		// wPreview.addListener( SWT.Selection, e -> preview() );

		// Add listeners 1.7
		wCancel.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				cancel();
			}
		});
		wOK.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				ok();
			}
		});
		wTest.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				test();
			}
		});
		wPreview.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				preview();
			}
		});

		// SelectionAdapter selAdapter = new SelectionAdapter() {
		// public void widgetDefaultSelected(SelectionEvent e) {
		// ok();
		// }
		// };

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);
		wHost.addSelectionListener(lsDef);
		wPort.addSelectionListener(lsDef);
		wUsername.addSelectionListener(lsDef);
		wPassword.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		// Set the shell size, based upon previous time...
		setSize();

		getData();
		input.setChanged(changed);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return stepname;

	}

	private void cancel() {
		stepname = null;
		input.setChanged(changed);
		dispose();
	}

	public void getData() {

		wStepname.setText(Const.NVL(stepname, ""));
		wHost.setText(Const.NVL(input.getHost(), ""));
		wPort.setText(Const.NVL(input.getPort(), ""));
		wUsername.setText(Const.NVL(input.getUsername(), ""));
		wPassword.setText(Const.NVL(input.getPassword(), ""));

		wQuery.setText(Const.NVL(input.getQuery(), ""));

		for (int i = 0; i < input.getReturnValues().size(); i++) {
			ReturnValue returnValue = input.getReturnValues().get(i);
			TableItem item = wReturns.table.getItem(i);
			item.setText(1, Const.NVL(returnValue.getName(), ""));
			item.setText(2, Const.NVL(returnValue.getSplunkName(), ""));
			item.setText(3, Const.NVL(returnValue.getType(), ""));
			item.setText(
					4,
					returnValue.getLength() < 0 ? "" : Integer
							.toString(returnValue.getLength()));
			item.setText(5, Const.NVL(returnValue.getFormat(), ""));
		}
		wReturns.removeEmptyRows();
		wReturns.setRowNums();
		wReturns.optWidth(true);

	}

	private void ok() {
		if (StringUtils.isEmpty(wStepname.getText())) {
			return;
		}
		stepname = wStepname.getText(); // return value
		getInfo(input);
		dispose();
	}

	private void getInfo(SplunkInputMeta meta) {
		meta.setHost(wHost.getText());
		meta.setPort(wPort.getText());
		meta.setUsername(wUsername.getText());
		meta.setPassword(wPassword.getText());
		meta.setQuery(wQuery.getText());

		List<ReturnValue> returnValues = new ArrayList<>();
		for (int i = 0; i < wReturns.nrNonEmpty(); i++) {
			TableItem item = wReturns.getNonEmpty(i);
			String name = item.getText(1);
			String splunkName = item.getText(2);
			String type = item.getText(3);
			int length = Const.toInt(item.getText(4), -1);
			String format = item.getText(5);
			returnValues.add(new ReturnValue(name, splunkName, type, length,
					format));
		}
		meta.setReturnValues(returnValues);
	}

	public void test() {

		SplunkConnection splunk = new SplunkConnection(stepname,
				wHost.getText(), wPort.getText(), wUsername.getText(),
				wPassword.getText()); 
		try {
			splunk.test();
			MessageBox box = new MessageBox(shell, SWT.OK);
			box.setText("OK");
			String message = "Connection successful!" + Const.CR;
			message += Const.CR;
			message += "Hostname : " + splunk.getRealHostname() + ", port : "
					+ splunk.getRealPort() + ", user : "
					+ splunk.getRealUsername();
			box.setMessage(message);
			box.open();
		} catch (Exception e) {
			new ErrorDialog(shell, "Error",
					"Error connecting to Splunk with Hostname '"
							+ splunk.getRealHostname() + "', port "
							+ splunk.getRealPort() + ", and username '"
							+ splunk.getRealUsername(), e);
		}
	}

	private synchronized void preview() {
		SplunkInputMeta oneMeta = new SplunkInputMeta();
		this.getInfo(oneMeta);
		TransMeta previewMeta = TransPreviewFactory
				.generatePreviewTransformation(this.transMeta, oneMeta,
						this.wStepname.getText());
		this.transMeta
				.getVariable("Internal.Transformation.Filename.Directory");
		previewMeta.getVariable("Internal.Transformation.Filename.Directory");
		EnterNumberDialog numberDialog = new EnterNumberDialog(this.shell,
				this.props.getDefaultPreviewSize(), BaseMessages.getString(PKG,
						"QueryDialog.PreviewSize.DialogTitle"),
				BaseMessages.getString(PKG,
						"QueryDialog.PreviewSize.DialogMessage"));
		int previewSize = numberDialog.open();
		if (previewSize > 0) {
			TransPreviewProgressDialog progressDialog = new TransPreviewProgressDialog(
					this.shell, previewMeta,
					new String[] { this.wStepname.getText() },
					new int[] { previewSize });
			progressDialog.open();
			Trans trans = progressDialog.getTrans();
			String loggingText = progressDialog.getLoggingText();
			if (!progressDialog.isCancelled() && trans.getResult() != null
					&& trans.getResult().getNrErrors() > 0L) {
				EnterTextDialog etd = new EnterTextDialog(this.shell,
						BaseMessages.getString(PKG,
								"System.Dialog.PreviewError.Title",
								new String[0]), BaseMessages.getString(PKG,
								"System.Dialog.PreviewError.Message",
								new String[0]), loggingText, true);
				etd.setReadOnly();
				etd.open();
			}

			PreviewRowsDialog prd = new PreviewRowsDialog(
					this.shell,
					this.transMeta,
					0,
					this.wStepname.getText(),
					progressDialog.getPreviewRowsMeta(this.wStepname.getText()),
					progressDialog.getPreviewRows(this.wStepname.getText()),
					loggingText);
			prd.open();
		}
	}

	private void getReturnValues() {

		try {

			SplunkConnection splunk = new SplunkConnection(stepname,
					wHost.getText(), wPort.getText(), wUsername.getText(),
					wPassword.getText()); 
			Service service = Service
					.connect(splunk.getServiceArgs());
			Args args = new Args();
			args.put("connection_mode", JobArgs.ExecutionMode.BLOCKING.name());

			InputStream eventsStream = service.oneshotSearch(
					transMeta.environmentSubstitute(wQuery.getText()), args);

			Set<String> detectedKeys = new HashSet<>();
			try {
				ResultsReaderXml resultsReader = new ResultsReaderXml(
						eventsStream);
				HashMap<String, String> event;
				int nrScanned = 0;
				while ((event = resultsReader.getNextEvent()) != null) {
					for (String key : event.keySet()) {
						detectedKeys.add(key);
					}
					nrScanned++;
					if (nrScanned > 10) {
						break;
					}
				}
			} finally {
				eventsStream.close();
			}

			for (String detectedKey : detectedKeys) {
				TableItem item = new TableItem(wReturns.table, SWT.NONE);
				item.setText(1, detectedKey);
				item.setText(2, detectedKey);
				item.setText(3, "String");
			}
			wReturns.removeEmptyRows();
			wReturns.setRowNums();
			wReturns.optWidth(true);

		} catch (Exception e) {
			new ErrorDialog(shell, "Error",
					"Error getting fields from Splunk query", e);
		}
	}
}
