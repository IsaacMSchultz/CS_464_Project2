
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from position.idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

#pragma once

struct DDS_TypeCode;

using namespace System;
using namespace DDS;

public ref struct Position
:  public DDS::ICopyable<Position^> {
    // --- Declared members: -------------------------------------------------
  public: 

    System::String^ timestamp;
    System::String^ route;
    System::String^ vehicle;
    System::Int32 stopNumber;
    System::Int32 numStops;
    System::Int32 timeBetweenStops;
    System::String^ trafficConditions;
    System::Int32 fillInRatio;

    // --- Static constants: -------------------------------------    
  public:

    // --- Constructors and destructors: -------------------------------------
  public:
    Position();

    // --- Utility methods: --------------------------------------------------
  public:

    virtual void clear() ;

    virtual System::Boolean copy_from(Position^ src);

    virtual System::Boolean Equals(System::Object^ other) override;
    static DDS::TypeCode^ get_typecode();

  private:
    static DDS::TypeCode^ _typecode;

}; // class Position

public ref class PositionSeq sealed
: public DDS::UserRefSequence<Position^> {
  public:
    PositionSeq() :
        DDS::UserRefSequence<Position^>() {
            // empty
    }
    PositionSeq(System::Int32 max) :
        DDS::UserRefSequence<Position^>(max) {
            // empty
    }
    PositionSeq(PositionSeq^ src) :
        DDS::UserRefSequence<Position^>(src) {
            // empty
    }
};

#define NDDSUSERDllExport

NDDSUSERDllExport DDS_TypeCode* Position_get_typecode();

