# Chapter 8 sample web service

This directory contains source code for the core of this sample, a web service supporting online shopping.

OnlineStore.jws contains the web service code, implementing logic to manage a shopping cart, retrieve catalog items, and check out. To manage inventory, the web service uses an EJB control (included as CreditCheckControl.ctrl) that retrieves items available from an Enterprise Java Bean (a session bean). To perform a "credit check", the web service uses a JMS control to request approval from an EJB message-driven bean via the Java Message Service (JMS).

| File | Description |
| --------- | ----------- |
| [`CreditCheckControl.ctrl`](CreditCheckControl.ctrl) | . |
| [`InventoryControl.ctrl`](InventoryControl.ctrl) | . |
| [`OnlineStore.jws`](OnlineStore.jws) | . |
