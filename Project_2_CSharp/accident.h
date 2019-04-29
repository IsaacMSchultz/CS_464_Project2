
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from accident.idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

#pragma once

struct DDS_TypeCode;

using namespace System;
using namespace DDS;

public ref struct Accident
:  public DDS::ICopyable<Accident^> {
    // --- Declared members: -------------------------------------------------
  public: 

    System::String^ timestamp;
    System::String^ route;
    System::String^ vehicle;
    System::Int32 stopNumber;

    // --- Static constants: -------------------------------------    
  public:

    // --- Constructors and destructors: -------------------------------------
  public:
    Accident();

    // --- Utility methods: --------------------------------------------------
  public:

    virtual void clear() ;

    virtual System::Boolean copy_from(Accident^ src);

    virtual System::Boolean Equals(System::Object^ other) override;
    static DDS::TypeCode^ get_typecode();

  private:
    static DDS::TypeCode^ _typecode;

}; // class Accident

public ref class AccidentSeq sealed
: public DDS::UserRefSequence<Accident^> {
  public:
    AccidentSeq() :
        DDS::UserRefSequence<Accident^>() {
            // empty
    }
    AccidentSeq(System::Int32 max) :
        DDS::UserRefSequence<Accident^>(max) {
            // empty
    }
    AccidentSeq(AccidentSeq^ src) :
        DDS::UserRefSequence<Accident^>(src) {
            // empty
    }
};

#define NDDSUSERDllExport

NDDSUSERDllExport DDS_TypeCode* Accident_get_typecode();

