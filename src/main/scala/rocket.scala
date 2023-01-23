package Vivado

import Chisel._
import freechips.rocketchip.config.{Field, Config, Parameters}
import freechips.rocketchip.devices.debug.DebugModuleKey
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.tile.{BuildRoCC, OpcodeSet}
import freechips.rocketchip.util.DontTouch
import freechips.rocketchip.system._

import chisel3.experimental.{annotate,ChiselAnnotation}
import firrtl.AttributeAnnotation

class RocketChip(implicit val p: Parameters) extends Module {

  val target = Module(LazyModule(new RocketSystem).module)

  require(target.mem_axi4.size == 1)
  require(target.mmio_axi4.size == 1)
  require(target.dma_axi4.size == 1)
  require(target.debug.head.systemjtag.size == 1)

  val io = IO(new Bundle {
    val mmio_axi4 = target.mmio_axi4.head.cloneType
    val mem_axi4 = target.mem_axi4.head.cloneType
    val dma_axi4 = Flipped(target.dma_axi4.head.cloneType)
    val interrupts = Input(UInt(p(NExtTopInterrupts).W))
  })

  val boardJTAG = Module(new BscanJTAG)
  val jtagBundle = target.debug.head.systemjtag.head

  // set JTAG parameters
  jtagBundle.reset := reset
  jtagBundle.mfr_id := 0x233.U(11.W)
  jtagBundle.part_number := 0.U(16.W)
  jtagBundle.version := 0.U(4.W)
  // connect to BSCAN
  jtagBundle.jtag.TCK := boardJTAG.tck
  jtagBundle.jtag.TMS := boardJTAG.tms
  jtagBundle.jtag.TDI := boardJTAG.tdi
  boardJTAG.tdo := jtagBundle.jtag.TDO.data
  boardJTAG.tdoEnable := jtagBundle.jtag.TDO.driven

  io.mmio_axi4 <> target.mmio_axi4.head
  io.mem_axi4 <> target.mem_axi4.head
  target.dma_axi4.head <> io.dma_axi4
  target.interrupts := RegNext(RegNext(RegNext(io.interrupts)))

  target.dontTouchPorts()

  Array(
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(reset.toTarget, "X_INTERFACE_INFO = \"xilinx.com:signal:reset:1.0 reset RST\", X_INTERFACE_PARAMETER = \"POLARITY ACTIVE_HIGH\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(clock.toTarget, "X_INTERFACE_INFO = \"xilinx.com:signal:clock:1.0 clock CLK\", X_INTERFACE_PARAMETER = \"ASSOCIATED_RESET reset ASSOCIATED_BUSIF MEM_AXI4:DMA_AXI4:IO_AXI4\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWREADY\", X_INTERFACE_PARAMETER = \"CLK_DOMAIN clock, PROTOCOL AXI4, ADDR_WIDTH 34, DATA_WIDTH 64\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.bits.addr.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWADDR\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.bits.len.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWLEN\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.bits.size.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWSIZE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.bits.burst.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWBURST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.bits.lock.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWLOCK\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.bits.cache.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWCACHE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.bits.prot.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWPROT\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.aw.bits.qos.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 AWQOS\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.w.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 WREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.w.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 WVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.w.bits.data.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 WDATA\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.w.bits.strb.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 WSTRB\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.w.bits.last.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 WLAST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.b.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 BREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.b.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 BVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.b.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 BID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.b.bits.resp.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 BRESP\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.bits.addr.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARADDR\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.bits.len.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARLEN\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.bits.size.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARSIZE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.bits.burst.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARBURST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.bits.lock.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARLOCK\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.bits.cache.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARCACHE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.bits.prot.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARPROT\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.ar.bits.qos.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 ARQOS\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.r.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 RREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.r.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 RVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.r.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 RID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.r.bits.data.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 RDATA\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.r.bits.resp.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 RRESP\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mem_axi4.r.bits.last.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 MEM_AXI4 RLAST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWREADY\", X_INTERFACE_PARAMETER = \"CLK_DOMAIN clock, PROTOCOL AXI4, ADDR_WIDTH 34, DATA_WIDTH 64\"")
    },
   new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.bits.addr.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWADDR\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.bits.len.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWLEN\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.bits.size.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWSIZE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.bits.burst.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWBURST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.bits.lock.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWLOCK\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.bits.cache.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWCACHE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.bits.prot.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWPROT\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.aw.bits.qos.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 AWQOS\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.w.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 WREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.w.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 WVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.w.bits.data.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 WDATA\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.w.bits.strb.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 WSTRB\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.w.bits.last.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 WLAST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.b.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 BREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.b.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 BVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.b.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 BID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.b.bits.resp.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 BRESP\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.bits.addr.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARADDR\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.bits.len.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARLEN\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.bits.size.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARSIZE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.bits.burst.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARBURST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.bits.lock.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARLOCK\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.bits.cache.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARCACHE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.bits.prot.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARPROT\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.ar.bits.qos.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 ARQOS\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.r.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 RREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.r.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 RVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.r.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 RID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.r.bits.data.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 RDATA\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.r.bits.resp.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 RRESP\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.dma_axi4.r.bits.last.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 DMA_AXI4 RLAST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWREADY\", X_INTERFACE_PARAMETER = \"CLK_DOMAIN clock, PROTOCOL AXI4, ADDR_WIDTH 31, DATA_WIDTH 64\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.bits.addr.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWADDR\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.bits.len.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWLEN\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.bits.size.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWSIZE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.bits.burst.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWBURST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.bits.lock.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWLOCK\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.bits.cache.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWCACHE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.bits.prot.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWPROT\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.aw.bits.qos.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 AWQOS\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.w.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 WREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.w.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 WVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.w.bits.data.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 WDATA\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.w.bits.strb.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 WSTRB\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.w.bits.last.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 WLAST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.b.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 BREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.b.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 BVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.b.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 BID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.b.bits.resp.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 BRESP\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.bits.addr.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARADDR\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.bits.len.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARLEN\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.bits.size.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARSIZE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.bits.burst.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARBURST\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.bits.lock.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARLOCK\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.bits.cache.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARCACHE\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.bits.prot.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARPROT\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.ar.bits.qos.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 ARQOS\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.r.ready.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 RREADY\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.r.valid.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 RVALID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.r.bits.id.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 RID\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.r.bits.data.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 RDATA\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.r.bits.resp.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 RRESP\"")
    },
    new ChiselAnnotation {
      override def toFirrtl = AttributeAnnotation(io.mmio_axi4.r.bits.last.toTarget, "X_INTERFACE_INFO = \"xilinx.com:interface:aximm:1.0 IO_AXI4 RLAST\"")
    },
  ).foreach(annotate(_))

}

class RocketSystem(implicit p: Parameters) extends RocketSubsystem
    with HasAsyncExtInterrupts
    with CanHaveMasterAXI4MemPort
    with CanHaveMasterAXI4MMIOPort
    with CanHaveSlaveAXI4Port
{
  val bootROM  = p(BootROMLocated(location)).map { BootROM.attach(_, this, CBUS) }
  override lazy val module = new RocketSystemModuleImp(this)
}

class RocketSystemModuleImp[+L <: RocketSystem](_outer: L) extends RocketSubsystemModuleImp(_outer)
    with HasRTCModuleImp
    with HasExtInterruptsModuleImp
    with DontTouch
{
  lazy val mem_axi4 = _outer.mem_axi4
  lazy val mmio_axi4 = _outer.mmio_axi4
  lazy val dma_axi4 = _outer.l2_frontend_bus_axi4
}

class WithGemmini(mesh_size: Int, bus_bits: Int) extends Config((site, here, up) => {
  case BuildRoCC => up(BuildRoCC) ++ Seq(
    (p: Parameters) => {
      implicit val q = p
      implicit val v = implicitly[ValName]
      LazyModule(new gemmini.Gemmini(gemmini.GemminiConfigs.defaultConfig.copy(
        meshRows = mesh_size, meshColumns = mesh_size, dma_buswidth = bus_bits)))
    }
  )
  case SystemBusKey => up(SystemBusKey).copy(beatBytes = bus_bits/8)
})

class WithDebugProgBuf(prog_buf_words: Int, imp_break: Boolean) extends Config((site, here, up) => {
  case DebugModuleKey => up(DebugModuleKey, site).map(_.copy(nProgramBufferWords = prog_buf_words, hasImplicitEbreak = imp_break))
})

/*----------------- 32-bit RocketChip ---------------*/
/* Note: Linux not supported yet on 32-bit cores     */

/* 32-bit config, max memory 2GB */
class Rocket32BaseConfig extends Config(
  new WithBootROMFile("workspace/bootrom.img") ++
  new WithExtMemSize(0x80000000L) ++
  new WithNExtTopInterrupts(8) ++
  new WithDTS("freechips,rocketchip-vivado", Nil) ++
  new WithDebugSBA ++
  new WithEdgeDataBits(64) ++
  new WithCoherentBusTopology ++
  new WithoutTLMonitors ++
  new BaseConfig)

class Rocket32s1 extends Config(
  new WithNBreakpoints(8) ++
  new WithNSmallCores(1)  ++
  new WithRV32            ++
  new Rocket32BaseConfig)

class Rocket32s2 extends Config(
  new WithNBreakpoints(8) ++
  new WithNSmallCores(2)  ++
  new WithRV32            ++
  new Rocket32BaseConfig)

/* With exposed JTAG port */
class Rocket32s2j extends Config(
  new WithNBreakpoints(8) ++
  new WithJtagDTM         ++
  new WithNSmallCores(2)  ++
  new WithRV32            ++
  new Rocket32BaseConfig)

class Rocket32s4 extends Config(
  new WithNBreakpoints(8) ++
  new WithNSmallCores(4)  ++
  new WithRV32            ++
  new Rocket32BaseConfig)

class Rocket32s8 extends Config(
  new WithNBreakpoints(8) ++
  new WithNSmallCores(8)  ++
  new WithRV32            ++
  new Rocket32BaseConfig)

class Rocket32s16 extends Config(
  new WithNBreakpoints(8) ++
  new WithNSmallCores(16) ++
  new WithRV32            ++
  new Rocket32BaseConfig)

/*----------------- 64-bit RocketChip ---------------*/

/*
 * WithExtMemSize(0x380000000L) = 14GB (16GB minus 2GB for IO) is max supported by the base config.
 * Actual memory size depends on the target board.
 * The Makefile changes the size to correct value during build.
 * It also sets right core clock frequency.
 */
class RocketBaseConfig extends Config(
  new WithJtagDTM ++
  new WithBootROMFile("workspace/bootrom.img") ++
  new WithExtMemSize(0x380000000L) ++
  new WithNExtTopInterrupts(8) ++
  new WithDTS("freechips,rocketchip-vivado", Nil) ++
  new WithDebugSBA ++
  new WithEdgeDataBits(64) ++
  new WithCoherentBusTopology ++
  new WithoutTLMonitors ++
  new BaseConfig)

class RocketWideBusConfig extends Config(
  new WithBootROMFile("workspace/bootrom.img") ++
  new WithExtMemSize(0x380000000L) ++
  new WithNExtTopInterrupts(8) ++
  new WithDTS("freechips,rocketchip-vivado", Nil) ++
  new WithDebugSBA ++
  new WithEdgeDataBits(256) ++
  new WithCoherentBusTopology ++
  new WithoutTLMonitors ++
  new BaseConfig)

class Rocket64b1 extends Config(
  new WithNBreakpoints(8) ++
  new WithNBigCores(1)    ++
  new RocketBaseConfig)

class Rocket64b2 extends Config(
  new WithNBreakpoints(8) ++
  new WithNBigCores(2)    ++
  new RocketBaseConfig)

/* With exposed BSCAN port - the name must end with 'e' */
/* With up to 256GB memory */
/* Note: lower 2GB are used for memory mapped IO, so max usable RAM size is 254GB */
class Rocket64b2e extends Config(
  new WithNBreakpoints(8) ++
  new WithNBigCores(2)    ++
  new WithExtMemSize(0x3f80000000L) ++
  new RocketBaseConfig)

/* With up to 256GB memory */
/* Note: lower 2GB are used for memory mapped IO, so max usable RAM size is 254GB */
class Rocket64b2m extends Config(
  new WithNBreakpoints(8) ++
  new WithNBigCores(2)    ++
  new WithExtMemSize(0x3f80000000L) ++
  new RocketBaseConfig)

/* With up to 256GB memory, 2 memory channels, L2 cache and wide memory bus */
class Rocket64b2m2 extends Config(
  new WithNBreakpoints(8) ++
  new WithNBigCores(2)    ++
  new WithExtMemSize(0x3f80000000L) ++
  new WithNMemoryChannels(2) ++
  new WithNBanks(4) ++ 
  new WithInclusiveCache ++
  new RocketWideBusConfig)

/* With up to 256GB memory, 4 memory channels, L2 cache and wide memory bus */
class Rocket64b4m4 extends Config(
  new WithNBreakpoints(8) ++
  new WithNBigCores(4)    ++
  new WithExtMemSize(0x3f80000000L) ++
  new WithNMemoryChannels(4) ++
  new WithNBanks(8) ++ 
  new WithInclusiveCache ++
  new RocketWideBusConfig)

/* With exposed JTAG port */
class Rocket64b2j extends Config(
  new WithNBreakpoints(8) ++
  new WithJtagDTM         ++
  new WithNBigCores(2)    ++
  new RocketBaseConfig)

/* Smaller debug module */
class Rocket64b2d1 extends Config(
  new WithNBreakpoints(1) ++
  new WithNBigCores(2)    ++
  new WithDebugProgBuf(1, true) ++
  new RocketBaseConfig)

/* Smaller debug module */
class Rocket64b2d2 extends Config(
  new WithNBreakpoints(2) ++
  new WithNBigCores(2)    ++
  new WithDebugProgBuf(2, true) ++
  new RocketBaseConfig)

/* Smaller debug module */
class Rocket64b2d3 extends Config(
  new WithNBreakpoints(3) ++
  new WithNBigCores(2)    ++
  new WithDebugProgBuf(2, false) ++
  new RocketBaseConfig)

/* With 512KB level 2 cache */
/* Note: adding L2 cache reduces max CPU clock frequency */
class Rocket64b2l2 extends Config(
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new WithNBigCores(2)    ++
  new RocketBaseConfig)

/* With Gemmini 4x4 and 2 small cores */
/* Note: small core has no MMU and cannot boot mainstream Linux */
class Rocket64s2gem4 extends Config(
  new WithGemmini(4, 64)  ++
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new WithNSmallCores(2)  ++
  new RocketBaseConfig)

/* With Gemmini 4x4 and 2 medium cores */
/* Note: cannot get medium core to boot Linux: Oops - illegal instruction */
class Rocket64m2gem4 extends Config(
  new WithGemmini(4, 64)  ++
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new WithNMedCores(2)    ++
  new RocketBaseConfig)

/* With Gemmini 4x4 */
class Rocket64b1gem4 extends Config(
  new WithGemmini(4, 64)  ++
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new WithNBigCores(1)    ++
  new RocketBaseConfig)

/* With Gemmini 8x8 */
class Rocket64b1gem8 extends Config(
  new WithGemmini(8, 64)  ++
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new WithNBigCores(1)    ++
  new RocketBaseConfig)

/* With Gemmini 16x16 */
class Rocket64b1gem16 extends Config(
  new WithGemmini(16, 64)  ++
  new WithInclusiveCache() ++
  new WithNBreakpoints(8) ++
  new WithNBigCores(1)    ++
  new RocketBaseConfig)

/* With Gemmini 4x4, 2 big cores */
class Rocket64b2gem4 extends Config(
  new WithGemmini(4, 64)  ++
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new WithNBigCores(2)    ++
  new RocketBaseConfig)

/* With Gemmini 8x8, 2 big cores */
class Rocket64b2gem8 extends Config(
  new WithGemmini(8, 64)  ++
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new WithNBigCores(2)    ++
  new RocketBaseConfig)

/* With Gemmini 16x16, 2 big cores */
class Rocket64b2gem16 extends Config(
  new WithGemmini(16, 64)  ++
  new WithInclusiveCache() ++
  new WithNBreakpoints(8) ++
  new WithNBigCores(2)    ++
  new RocketBaseConfig)

class Rocket64b4 extends Config(
  new WithNBreakpoints(8) ++
  new WithNBigCores(4)    ++
  new RocketBaseConfig)

/* With level 2 cache and wide memory bus */
class Rocket64b4l2w extends Config(
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new WithNBigCores(4)    ++
  new RocketWideBusConfig)

class Rocket64b8 extends Config(
  new WithNBreakpoints(8) ++
  new WithNBigCores(8)    ++
  new RocketBaseConfig)

class Rocket64b16m extends Config(
  new WithNBreakpoints(4) ++
  new WithNBigCores(16)   ++
  new WithExtMemSize(0x3f80000000L) ++
  new RocketBaseConfig)

class Rocket64b24m extends Config(
  new WithNBreakpoints(4) ++
  new WithNBigCores(24)   ++
  new WithExtMemSize(0x3f80000000L) ++
  new RocketBaseConfig)

class Rocket64b32m extends Config(
  new WithNBreakpoints(4) ++
  new WithNBigCores(32)   ++
  new WithExtMemSize(0x3f80000000L) ++
  new RocketBaseConfig)

/* Without slave port - for use in HDL simulation */
class Rocket64b2s extends Config(
  new WithNBigCores(2)    ++
  new WithBootROMFile("workspace/bootrom.img") ++
  new WithExtMemSize(0x40000000) ++
  new WithNExtTopInterrupts(8) ++
  new WithEdgeDataBits(64) ++
  new WithCoherentBusTopology ++
  new WithoutTLMonitors ++
  new WithNoSlavePort ++
  new BaseConfig)

/*----------------- Sonic BOOM   ---------------*/

class Rocket64w1 extends Config(
  new WithNBreakpoints(8) ++
  new boom.common.WithNSmallBooms(1) ++
  new RocketBaseConfig)

class Rocket64x1 extends Config(
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new boom.common.WithNMediumBooms(1) ++
  new RocketWideBusConfig)

/* Note: multi-core BOOM appears unstable */
class Rocket64x2 extends Config(
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new boom.common.WithNMediumBooms(2) ++
  new RocketWideBusConfig)

class Rocket64x4 extends Config(
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new boom.common.WithNMediumBooms(4) ++
  new RocketWideBusConfig)

class Rocket64x8 extends Config(
  new WithInclusiveCache  ++
  new WithNBreakpoints(4) ++
  new boom.common.WithNMediumBooms(8) ++
  new RocketWideBusConfig)

class Rocket64x12 extends Config(
  new WithInclusiveCache  ++
  new WithNBreakpoints(4) ++
  new boom.common.WithNMediumBooms(12) ++
  new RocketWideBusConfig)

/* With up to 256GB memory, 4 memory channels */
/* Note: lower 2GB are used for memory mapped IO, so max usable RAM size is 254GB */
class Rocket64x12m4 extends Config(
  new WithNBreakpoints(4) ++
  new boom.common.WithNMediumBooms(12) ++
  new WithExtMemSize(0x3f80000000L) ++
  new WithNMemoryChannels(4) ++
  new WithNBanks(8) ++ 
  new WithInclusiveCache ++
  new RocketWideBusConfig)

/* Note: 3-way BOOM appears unstable */
class Rocket64y1 extends Config(
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new boom.common.WithNLargeBooms(1) ++
  new RocketWideBusConfig)

/* Note: 4-way BOOM appears unstable */
class Rocket64z1 extends Config(
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new boom.common.WithNMegaBooms(1) ++
  new RocketWideBusConfig)

/* With up to 256GB memory */
/* Note: lower 2GB are used for memory mapped IO, so max usable RAM size is 254GB */
/* Note: 4-way BOOM appears unstable */
class Rocket64z2m extends Config(
  new WithInclusiveCache  ++
  new WithNBreakpoints(8) ++
  new boom.common.WithNMegaBooms(2) ++
  new WithExtMemSize(0x3f80000000L) ++
  new RocketWideBusConfig)
