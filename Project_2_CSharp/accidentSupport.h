/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from accident.idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

#pragma once

#include "accident.h"

class DDSDataWriter;
class DDSDataReader;

// ---------------------------------------------------------------------------
// AccidentTypeSupport
// ---------------------------------------------------------------------------

ref class AccidentPlugin;

/* A collection of useful methods for dealing with objects of type
* Accident.
*/
public ref class AccidentTypeSupport
: public DDS::TypedTypeSupport<Accident^> {
    // --- Type name: --------------------------------------------------------
  public:
    static System::String^ TYPENAME = "Accident";

    // --- Public Methods: ---------------------------------------------------
  public:
    /* Get the default name of this type.
    *
    * An application can choose to register a type under any name, so
    * calling this method is strictly optional.
    */
    static System::String^ get_type_name();

    /* Register this type with the given participant under the given logical
    * name. This type must be registered before a Topic can be created that
    * uses it.
    */
    static void register_type(
        DDS::DomainParticipant^ participant,
        System::String^ type_name);

    /* Unregister this type from the given participant, where it was
    * previously registered under the given name. No further Topic creation
    * using this type will be possible.
    *
    * Unregistration allows some middleware resources to be reclaimed.
    */
    static void unregister_type(
        DDS::DomainParticipant^ participant,
        System::String^ type_name);

    /* Create an instance of the Accident type.
    */
    static Accident^ create_data();

    /* If instances of the Accident type require any
    * explicit finalization, perform it now on the given sample.
    */
    static void delete_data(Accident^ data);

    /* Write the contents of the data sample to standard out.
    */
    static void print_data(Accident^ a_data);

    /* Perform a deep copy of the contents of one data sample over those of
    * another, overwriting it.
    */
    static void copy_data(
        Accident^ dst_data,
        Accident^ src_data);

    static void serialize_data_to_cdr_buffer(
        array<System::Byte>^ buffer,
        System::UInt32% length,
        Accident^ a_data);

    static void deserialize_data_from_cdr_buffer(
        Accident^ a_data,
        array<System::Byte>^ buffer,
        System::UInt32 length);

    static System::String^ data_to_string(
        Accident ^sample,
        PrintFormatProperty ^property);

    static System::String^ data_to_string(
        Accident ^sample);

    static DDS::TypeCode^ get_typecode();

    // --- Implementation: ---------------------------------------------------
    /* The following code is for the use of the middleware infrastructure.
    * Applications are not expected to call it directly.
    */
  public:
    virtual System::String^ get_type_name_untyped() override;
    virtual DDS::DataReader^ create_datareaderI(
        System::IntPtr impl) override;
    virtual DDS::DataWriter^ create_datawriterI(
        System::IntPtr impl) override;

    virtual Accident^ create_data_untyped() override;

  public:
    static AccidentTypeSupport^ get_instance();

    AccidentTypeSupport();

  private:
    static AccidentTypeSupport^ _singleton;
    AccidentPlugin^ _type_plugin;
};

// ---------------------------------------------------------------------------
// AccidentDataReader
// ---------------------------------------------------------------------------

/**
* A reader for the Accident type.
*/
public ref class AccidentDataReader :
public DDS::TypedDataReader<Accident^> {
    /* The following code is for the use of the middleware infrastructure.
    * Applications are not expected to call it directly.
    */
    internal:
    AccidentDataReader(System::IntPtr impl);
};

// ---------------------------------------------------------------------------
// AccidentDataWriter
// ---------------------------------------------------------------------------

/**
* A writer for the Accident user type.
*/
public ref class AccidentDataWriter :
public DDS::TypedDataWriter<Accident^> {
    /* The following code is for the use of the middleware infrastructure.
    * Applications are not expected to call it directly.
    */
    internal:
    AccidentDataWriter(System::IntPtr impl);
};
