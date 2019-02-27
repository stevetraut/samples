# Chapter 8 sample EJB sources

This directory contains implementation and supporting code for the EJBs used by the web service sample.

The EJB sources aren't, strictly speaking, sample code. The sample is the OnlineStore.jws web service and the two controls that it uses. Using build scripts, the user compiles and deploys the EJBs.

| Directory | Description |
| --------- | ----------- |
| [`creditcheck_08`](creditcheck_08) | An EJB that is the backend of a JMS-carried request for a credit check. |
| [`database`](database) | DDL and sample data to support a database with items in inventory, and which can be purchased via the online store. |
| [`inventory`](inventory) | EJBs representing items in inventory whose values are stored in the database. |
