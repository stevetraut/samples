# Control Features

These folders each contain code illustrating a different aspect of custom control development for WebLogic Workshop. 

[controlEvents](controlEvents) | Control events are simply callbacks. The files here are Java interface and implementation files for requesting and handling callbacks.
[images](images) | Images displayed in the WebLogic Workshop IDE for the sample controls.
[insertWizard](insertWizard) | Java Swing code implementing UI for a dialog and a wizard to present to the web service developer when they add an instance of this control to the IDE. Extends a base class provided with the SDK.
[insertWizardCustom](insertWizardCustom) | Code implementing a fully−custom insert dialog for a control. The wizard in this example implements an insert dialog that does not inherit user interface components in a dialog provided by WebLogic Workshop.
[jcxCreate](jcxCreate) | Implements support for generating a JCX file when the user adds the control to a WebLogic Workshop project. This sample also includes a custom attribute editor.
[propEditor](propEditor) | Implements support for UI and logic to support design-time editing and validation of control properties.

